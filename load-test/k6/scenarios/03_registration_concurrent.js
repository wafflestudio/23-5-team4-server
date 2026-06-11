import http from 'k6/http';
import { check, fail } from 'k6';
import { Rate } from 'k6/metrics';
import { BASE_URL, THRESHOLDS, authHeaders } from '../config.js';
import { login } from '../helpers/auth.js';
import { createEvent, makeUserCredentials } from '../helpers/data.js';

const VU_COUNT = 15;
const CAPACITY = 4;
const registrationConsistency = new Rate('registration_consistency');

export const options = {
  thresholds: {
    ...THRESHOLDS,
    // 신청 자체는 정원 초과여도 200이므로 실패율 기준 유지
    http_req_failed: [{ threshold: 'rate<0.01' }],
    // 핵심 경로(등록 API)만 따로 latency threshold를 건다.
    'http_req_duration{endpoint:registration_create}': [
      { threshold: 'p(95)<500', abortOnFail: false },
      { threshold: 'p(99)<1000', abortOnFail: false },
    ],
    // teardown에서 기록하는 정합성 지표
    registration_consistency: [{ threshold: 'rate==1' }],
  },
  // 각 VU가 정확히 1회씩 실행되도록 고정한다.
  scenarios: {
    registration_concurrent: {
      executor: 'per-vu-iterations',
      vus: VU_COUNT,
      iterations: 1,
      maxDuration: '1m',
    },
  },
};

export function setup() {
  // 호스트 계정으로 이벤트 생성
  const hostToken = login('loadtest-host@example.com', 'password123!');
  const publicId = createEvent(hostToken, { capacity: CAPACITY, waitlistEnabled: true });
  console.log(`이벤트 생성 완료: ${BASE_URL}/api/events/${publicId}`);

  // 참여자 토큰 미리 발급 (setup에서 직렬 처리)
  const tokens = [];
  for (let i = 0; i < VU_COUNT; i++) {
    const { email, password } = makeUserCredentials(i);
    tokens.push(login(email, password));
  }

  return { publicId, tokens };
}

export default function ({ publicId, tokens }) {
  const token = tokens[__VU - 1];

  const res = http.post(
    `${BASE_URL}/api/events/${publicId}/registrations`,
    JSON.stringify({}),
    {
      headers: authHeaders(token),
      tags: { endpoint: 'registration_create' },
    },
  );

  check(res, {
    'registration 200': (r) => r.status === 200,
    'registrationPublicId exists': (r) => !!r.json('registrationPublicId'),
  });
}

export function teardown({ publicId }) {
  // 결과 요약: CONFIRMED / WAITLISTED 수량 출력 (페이지네이션 처리)
  const hostToken = login('loadtest-host@example.com', 'password123!');

  let allRegistrations = [];
  let cursor = null;

  do {
    const url = cursor
      ? `${BASE_URL}/api/events/${publicId}/registrations?cursor=${cursor}`
      : `${BASE_URL}/api/events/${publicId}/registrations`;

    const res = http.get(url, { headers: authHeaders(hostToken) });
    const body = res.json();

    allRegistrations = allRegistrations.concat(body.participants || []);
    cursor = body.hasNext ? body.nextCursor : null;
  } while (cursor !== null);

  const confirmed = allRegistrations.filter((r) => r.status === 'CONFIRMED').length;
  const waitlisted = allRegistrations.filter((r) => r.status === 'WAITLISTED').length;
  const expectedWaitlisted = VU_COUNT - CAPACITY;
  const isConsistent =
    confirmed === CAPACITY &&
    waitlisted === expectedWaitlisted &&
    allRegistrations.length === VU_COUNT;

  console.log(`CONFIRMED: ${confirmed} (expected: ${CAPACITY})`);
  console.log(`WAITLISTED: ${waitlisted} (expected: ${expectedWaitlisted})`);
  console.log(`TOTAL: ${allRegistrations.length} (expected: ${VU_COUNT})`);
  console.log(`동시성 이상 없음: ${isConsistent ? 'PASS' : 'FAIL'}`);

  registrationConsistency.add(isConsistent ? 1 : 0);
  check(null, {
    'concurrency consistency': () => isConsistent,
  });
  if (!isConsistent) {
    fail(
      `concurrency mismatch: confirmed=${confirmed}, waitlisted=${waitlisted}, total=${allRegistrations.length}`,
    );
  }
}
