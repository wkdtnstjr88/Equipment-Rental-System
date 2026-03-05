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