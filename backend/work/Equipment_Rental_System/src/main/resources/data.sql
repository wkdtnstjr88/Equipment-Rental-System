INSERT IGNORE INTO equipment (name, category, daily_price) VALUES
('iPad Pro 12.9', 'タブレット', 15000),
('Sony A7R5', 'カメラ', 30000),
('MacBook Pro 16', 'ノートPC', 25000);

INSERT IGNORE INTO equipment_item (equipment_id, serial_number, status) VALUES
(1, 'SN-PAD-001', 'AVAILABLE'),
(1, 'SN-PAD-002', 'AVAILABLE'),
(2, 'SN-SONY-001', 'AVAILABLE'),
(2, 'SN-SONY-002', 'AVAILABLE'),
(3, 'SN-MBP-001', 'AVAILABLE'),
(3, 'SN-MBP-002', 'AVAILABLE'),
(3, 'SN-MBP-003', 'AVAILABLE');
