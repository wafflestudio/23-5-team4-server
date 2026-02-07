-- 회원 중복 방지: (event_id, user_id)
ALTER TABLE registrations
  ADD CONSTRAINT uq_registrations_event_user UNIQUE (event_id, user_id);

-- 비회원 중복 방지: (event_id, guest_email)
ALTER TABLE registrations
  ADD CONSTRAINT uq_registrations_event_guest_email UNIQUE (event_id, guest_email);
