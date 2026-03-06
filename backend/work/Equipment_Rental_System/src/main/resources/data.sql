-- 1. 장비 마스터 정보 (Equipment)
INSERT INTO equipment (id, name, category, daily_price) VALUES (1, '맥북 프로 16', '노트북', 50000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (2, '아이패드 프로 12.9', '태블릿', 30000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (3, '소니 A7R5', '카메라', 70000);
INSERT INTO equipment (id, name, category, daily_price) VALUES (4, '다이슨 에어랩', '생활가전', 15000);

-- 2. 실제 기기 재고 (EquipmentItem)
-- 맥북 2대
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (1, 'SN-MBP-001', 'AVAILABLE', 1);
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (2, 'SN-MBP-002', 'AVAILABLE', 1);

-- 아이패드 1대
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (3, 'SN-IPP-001', 'AVAILABLE', 2);

-- 카메라 1대 (이미 대여 중인 설정)
INSERT INTO equipment_item (id, serial_number, status, equipment_id) VALUES (4, 'SN-SONY-001', 'RENTED', 3);

-- 3. 대여 이력 샘플 데이터 (RentalHistory)

-- 케이스 1: 현재 '소니 카메라(item_id: 4)'를 대여 중인 이력
-- (위에서 item 4번을 'RENTED'로 설정하셨기 때문에 이 이력이 반드시 있어야 말이 됩니다!)
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, status)
VALUES (4, '홍길동', '2026-03-05 10:00:00', NULL, 'RENTED');

-- 케이스 2: '맥북(item_id: 1)'을 빌렸다가 이미 반납 완료한 과거 이력
-- (현재 item 1번이 'AVAILABLE'이므로, 과거에 빌렸던 기록이 있다고 설정합니다.)
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, status)
VALUES (1, '김철수', '2026-02-20 09:00:00', '2026-02-22 15:00:00', 'RETURNED');

-- 케이스 3: '아이패드(item_id: 3)'를 빌렸다가 반납 완료한 이력
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, status)
VALUES (3, '이영희', '2026-03-01 13:00:00', '2026-03-03 18:00:00', 'RETURNED');