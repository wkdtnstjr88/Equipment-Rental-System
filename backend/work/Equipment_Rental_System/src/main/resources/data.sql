-- 0. 기존 데이터 삭제 (자식 테이블부터 지워야 외래키 에러가 안 납니다)
DELETE FROM rental_history;
DELETE FROM equipment_item;
DELETE FROM equipment;

-- 1. 장비 모델 등록 (Equipment)
INSERT INTO equipment (id, name, category, daily_price) VALUES (1, '맥북 프로 16', '노트북', 50000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (2, '아이패드 프로 12.9', '태블릿', 30000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (3, '소니 A7R5', '카메라', 70000);

-- 2. 실제 개별 기기 등록 (EquipmentItem)
-- [맥북: 총 3대] 2대 대여 가능, 1대 대여 중
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (1, 'SN-MBP-001', 'AVAILABLE', 1);
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (2, 'SN-MBP-002', 'AVAILABLE', 1);
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (3, 'SN-MBP-003', 'RENTED', 1);

-- [아이패드: 총 1대] 1대 대여 가능
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (4, 'SN-PAD-001', 'AVAILABLE', 2);

-- [소니 카메라: 총 1대] 1대 대여 중 (품절 상태 테스트용)
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (5, 'SN-SONY-001', 'RENTED', 3);

-- 3. 대여 이력 등록 (RentalHistory)
-- 김철수: 맥북 1번을 빌렸다가 이미 반납함 (과거 기록)
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, history_status)
VALUES (1, '김철수', '2026-02-20 09:00:00', '2026-02-22 15:00:00', 'RETURNED');

-- 홍길동: 소니 카메라(5번)를 현재 빌리고 있음
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, history_status)
VALUES (5, '홍길동', '2026-03-05 10:00:00', NULL, 'RENTED');

-- 이영희: 맥북 3번을 현재 빌리고 있음
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, history_status)
VALUES (3, '이영희', '2026-03-07 14:00:00', NULL, 'RENTED');