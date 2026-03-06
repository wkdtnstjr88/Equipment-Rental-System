-- 기존 데이터를 싹 비우고 새로 넣는 방식 (중복 방지)
DELETE FROM rental_history;
DELETE FROM equipment_item;
DELETE FROM equipment;

-- 1. 장비 (ID 자동 생성에 맡기지 않고 명시)
INSERT INTO equipment (id, name, category, daily_price) VALUES (1, '맥북 프로 16', '노트북', 50000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (2, '아이패드 프로 12.9', '태블릿', 30000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (3, '소니 A7R5', '카메라', 70000);

-- 2. 아이템
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (1, 'SN-MBP-001', 'AVAILABLE', 1);
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (4, 'SN-SONY-001', 'RENTED', 3);

-- 3. 이력 (ID 없이 넣어서 DB가 알아서 번호를 매기게 함)
-- 만약 'id' 컬럼 때문에 에러가 난다면, 아예 id를 빼고 넣어보세요.
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, history_status)
VALUES (4, '홍길동', '2026-03-05 10:00:00', NULL, 'RENTED');

INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, history_status)
VALUES (1, '김철수', '2026-02-20 09:00:00', '2026-02-22 15:00:00', 'RETURNED');