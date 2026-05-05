// 실행 시 -e BASE_URL=https://moiming-dev-api.wafflestudio.com 으로 오버라이드 가능
// 예) k6 run -e BASE_URL=https://moiming-dev-api.wafflestudio.com scenarios/01_auth.js
export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const THRESHOLDS = {
  http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: false }],
  http_req_duration: [
    { threshold: 'p(95)<500', abortOnFail: false },
    { threshold: 'p(99)<1000', abortOnFail: false },
  ],
  checks: [{ threshold: 'rate>0.99', abortOnFail: false }],
};

export const DEFAULT_HEADERS = {
  'Content-Type': 'application/json',
};

export function authHeaders(token) {
  return {
    ...DEFAULT_HEADERS,
    Authorization: `Bearer ${token}`,
  };
}
