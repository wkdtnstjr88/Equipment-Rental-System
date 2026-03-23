-- 1. 備品マスター (Equipment Master)
INSERT INTO equipment (name, category, daily_price) VALUES
                                                        ('iPad Pro 12.9', 'タブレット', 15000),
                                                        ('Sony A7R5', 'カメラ', 30000),
                                                        ('MacBook Pro 16', 'ノートPC', 25000);

-- 2. 詳細機器 (全て AVAILABLE に設定)
INSERT INTO equipment_item (equipment_id, serial_number, status) VALUES
                                                                     (1, 'SN-PAD-001', 'AVAILABLE'),
                                                                     (1, 'SN-PAD-002', 'AVAILABLE'),
                                                                     (2, 'SN-SONY-001', 'AVAILABLE'),
                                                                     (2, 'SN-SONY-002', 'AVAILABLE'),
                                                                     (3, 'SN-MBP-001', 'AVAILABLE'),
                                                                     (3, 'SN-MBP-002', 'AVAILABLE'),
                                                                     (3, 'SN-MBP-003', 'AVAILABLE');

-- 3. レンタル履歴 (全て「返却完了」および日付を記入)
INSERT INTO rental_history (equipment_item_id, member_name, rental_date, return_date, history_status) VALUES
-- 既存の「レンタル中」だったユーザーも全て返却処理済みの付加
(1, 'ソ・イヒョン', '2026-03-10 10:00:00', '2026-03-10 18:00:00', '返却完了'),
(3, 'チャ・ウヌ', '2026-03-10 11:30:00', '2026-03-10 19:00:00', '返却完了'),
(5, 'キム・ヘス', '2026-03-09 09:00:00', '2026-03-09 18:00:00', '返却完了'),
(6, 'チョ・ジヌン', '2026-03-09 14:00:00', '2026-03-09 21:00:00', '返却完了');


