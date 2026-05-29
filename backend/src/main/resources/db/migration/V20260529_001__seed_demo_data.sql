-- V20260529_001: Seed demo data for development and testing
-- Password for all users: demo1234 (BCrypt hash)

-- ============================================================
-- 1. Users
-- ============================================================

INSERT INTO t_user (id, username, password_hash, user_type, status, preferred_language) VALUES
(1001, 'operator01', '$2b$10$rVffDE1GXPuwHMtV9qqROeSfrRPGTmUGz0iTGjTEFYw7Uj5jqDTW.', 'WAREHOUSE_OPERATOR', 'ENABLED', 'zh-CN'),
(1002, 'operator02', '$2b$10$rVffDE1GXPuwHMtV9qqROeSfrRPGTmUGz0iTGjTEFYw7Uj5jqDTW.', 'WAREHOUSE_OPERATOR', 'ENABLED', 'zh-CN'),
(1003, 'admin01',    '$2b$10$rVffDE1GXPuwHMtV9qqROeSfrRPGTmUGz0iTGjTEFYw7Uj5jqDTW.', 'ADMIN',               'ENABLED', 'zh-CN'),
(1004, 'customer01', '$2b$10$rVffDE1GXPuwHMtV9qqROeSfrRPGTmUGz0iTGjTEFYw7Uj5jqDTW.', 'CUSTOMER',             'ENABLED', 'zh-CN'),
(1005, 'customer02', '$2b$10$rVffDE1GXPuwHMtV9qqROeSfrRPGTmUGz0iTGjTEFYw7Uj5jqDTW.', 'CUSTOMER',             'ENABLED', 'en-US');

-- ============================================================
-- 2. User Profiles
-- ============================================================

INSERT INTO t_user_profile (id, user_id, display_name, contact_phone, email, nationality) VALUES
(1001, 1001, '仓管员 张伟',   '13800000001', 'zhangwei@tsingtaohui.test',     'CN'),
(1002, 1002, '仓管员 李明',   '13800000002', 'liming@tsingtaohui.test',       'CN'),
(1003, 1003, '管理员 王磊',   '13800000003', 'wanglei@tsingtaohui.test',      'CN'),
(1004, 1004, 'Captain Johnson', '13800000004', 'johnson@ship.test',            'US'),
(1005, 1005, '船长白帆',     '13800000005', 'baifan@ship.test',               'CN');

-- ============================================================
-- 3. User Ships (for customer accounts)
-- ============================================================

INSERT INTO t_user_ship (id, user_id, ship_no, ship_name, ship_nationality, imo, mmsi, is_default) VALUES
(1001, 1004, 'MAERSK-001',  'Maersk Elba',   'DK', 'IMO9876544', '412345679', 1),
(1002, 1005, 'COSCO-QD-001', '中远青岛号',   'CN', 'IMO9876543', '412345678', 1);

-- ============================================================
-- 4. Shipping Agents
-- ============================================================

INSERT INTO t_shipping_agent (id, agent_name_zh, agent_name_en, contact_name, contact_phone, status) VALUES
(1001, '青岛远洋代理',   'Qingdao Ocean Agency',   '赵经理',    '0532-88880001', 'ENABLED'),
(1002, '港通船舶服务',   'PortLink Ship Services',  'Mike Chen', '0532-88880002', 'ENABLED');

-- ============================================================
-- 5. Ships
-- ============================================================

INSERT INTO t_ship (id, ship_no, ship_name, ship_nationality, imo, mmsi, current_berth, current_anchorage, target_gps, location_source) VALUES
(1001, 'COSCO-QD-001', '中远青岛号',   'CN', 'IMO9876543', '412345678', 'BERTH-A3', NULL,          '36.0755,120.3844', 'ADMIN'),
(1002, 'MAERSK-001',   'Maersk Elba',  'DK', 'IMO9876544', '412345679', NULL,       'ANCHORAGE-B2', '36.0812,120.3901', 'SHIPXY'),
(1003, 'EVER-002',     '长荣二号',     'CN', 'IMO9876545', '412345680', 'BERTH-C1', NULL,          '36.0698,120.3767', 'MARINE_TRAFFIC');

-- ============================================================
-- 6. Product Categories
-- ============================================================

INSERT INTO t_product_category (id, parent_id, name_zh, name_en, sort_order, status) VALUES
(1001, NULL, '饮料', 'Beverages', 1, 'ENABLED'),
(1002, NULL, '食品', 'Food',      2, 'ENABLED');

-- ============================================================
-- 7. Products
-- ============================================================

INSERT INTO t_product (id, sku_code, category_id, name_zh, name_en, specification, price, weight_kg, volume_m3, source, drone_deliverable, status) VALUES
(1001, 'SKU-COKE-330',    1001, '可口可乐 330ml',     'Coca-Cola 330ml',               '330ml×24罐',  2.50,  0.360, 0.0004, 'BONDED_WAREHOUSE', 1, 'ON_SALE'),
(1002, 'SKU-WATER-550',   1001, '矿泉水 550ml',       'Mineral Water 550ml',           '550ml×24瓶',  1.80,  0.580, 0.0005, 'BONDED_WAREHOUSE', 1, 'ON_SALE'),
(1003, 'SKU-COFFEE-200',  1001, '即饮咖啡 200ml',     'Ready-to-drink Coffee 200ml',   '200ml×15瓶',  5.90,  0.220, 0.0003, 'BONDED_WAREHOUSE', 1, 'ON_SALE'),
(1004, 'SKU-BISCUIT-120', 1002, '黄油饼干 120g',      'Butter Biscuits 120g',          '120g×12盒',   3.80,  0.130, 0.0002, 'BONDED_WAREHOUSE', 1, 'ON_SALE'),
(1005, 'SKU-TEA-500',     1001, '瓶装绿茶 500ml',     'Green Tea 500ml',               '500ml×24瓶',  3.20,  0.530, 0.0005, 'BONDED_WAREHOUSE', 1, 'ON_SALE'),
(1006, 'SKU-NOODLE-110',  1002, '方便面 110g',        'Instant Noodles 110g',          '110g×20袋',   2.00,  0.120, 0.0002, 'BONDED_WAREHOUSE', 1, 'ON_SALE');

-- ============================================================
-- 8. Inventory
-- ============================================================

INSERT INTO t_inventory (id, warehouse_id, location_code, sku_code, batch_no, available_qty, locked_qty, outbound_qty, version) VALUES
(1001, 1, 'A-01-03', 'SKU-COKE-330',    'B20260501', 128, 16, 0, 0),
(1002, 1, 'A-02-08', 'SKU-WATER-550',   'B20260506',  42,  8, 0, 0),
(1003, 1, 'B-04-02', 'SKU-COFFEE-200',  'B20260420',   7,  5, 0, 0),
(1004, 1, 'C-02-11', 'SKU-BISCUIT-120', 'B20260319',   0, 12, 0, 0),
(1005, 1, 'A-03-06', 'SKU-TEA-500',     'B20260512',  23,  4, 0, 0),
(1006, 1, 'D-01-05', 'SKU-NOODLE-110',  'B20260430',   5,  9, 0, 0);

-- ============================================================
-- 9. Orders — 7 orders covering 3 warehouse statuses
-- ============================================================

-- 9a. Orders in WAREHOUSE_PROCESSING (picking phase, warehouse_status = NULL or PICKING)
-- These appear as picking tasks in the warehouse client.

INSERT INTO t_order (id, order_no, user_id, total_price, total_weight_kg, total_volume_m3, trade_mode,
    order_status, warehouse_status, delivery_status, customs_sync_status,
    consignee_name, cabin_no, contact_info, expected_delivery_time,
    ship_no, ship_name, ship_nationality, imo, mmsi, berth_or_anchorage, target_gps,
    shipping_agent_id, shipping_agent_name) VALUES
(2001, 'TH202605290001', 1004,  20.20,  2.640, 0.0032, 'AUTO_TRADE',
    'WAREHOUSE_PROCESSING', NULL, NULL, 'SYNC_SUCCESS',
    'Captain Johnson', 'A-204', '13800000004', '2026-05-29 16:00:00',
    'MAERSK-001', 'Maersk Elba', 'DK', 'IMO9876544', '412345679', 'ANCHORAGE-B2', '36.0812,120.3901',
    1001, '青岛远洋代理'),
(2002, 'TH202605290002', 1005,  23.60,  0.880, 0.0012, 'AUTO_TRADE',
    'WAREHOUSE_PROCESSING', NULL, NULL, 'SYNC_SUCCESS',
    '白帆', 'B-102', '13800000005', '2026-05-29 15:30:00',
    'COSCO-QD-001', '中远青岛号', 'CN', 'IMO9876543', '412345678', 'BERTH-A3', '36.0755,120.3844',
    1002, '港通船舶服务'),
(2003, 'TH202605290003', 1004,  16.00,  0.960, 0.0016, 'AUTO_TRADE',
    'WAREHOUSE_PROCESSING', 'PICKING', NULL, 'SYNC_SUCCESS',
    'Captain Johnson', 'A-204', '13800000004', '2026-05-29 17:00:00',
    'MAERSK-001', 'Maersk Elba', 'DK', 'IMO9876544', '412345679', 'ANCHORAGE-B2', '36.0812,120.3901',
    1001, '青岛远洋代理');

-- 9b. Orders picked, awaiting review (warehouse_status = PICKED)
-- These appear as review tasks.

INSERT INTO t_order (id, order_no, user_id, total_price, total_weight_kg, total_volume_m3, trade_mode,
    order_status, warehouse_status, delivery_status, customs_sync_status,
    consignee_name, cabin_no, contact_info,
    ship_no, ship_name, ship_nationality, imo, mmsi, berth_or_anchorage, target_gps,
    shipping_agent_id, shipping_agent_name) VALUES
(2004, 'TH202605290004', 1005,   9.00,  2.900, 0.0025, 'AUTO_TRADE',
    'WAREHOUSE_PROCESSING', 'PICKED', NULL, 'SYNC_SUCCESS',
    '白帆', 'C-305', '13800000005',
    'COSCO-QD-001', '中远青岛号', 'CN', 'IMO9876543', '412345678', 'BERTH-A3', '36.0755,120.3844',
    1002, '港通船舶服务'),
(2005, 'TH202605290005', 1004,  18.40,  2.830, 0.0028, 'AUTO_TRADE',
    'WAREHOUSE_PROCESSING', 'PICKED', NULL, 'SYNC_SUCCESS',
    'Captain Johnson', 'A-102', '13800000004',
    'MAERSK-001', 'Maersk Elba', 'DK', 'IMO9876544', '412345679', 'ANCHORAGE-B2', '36.0812,120.3901',
    1001, '青岛远洋代理');

-- 9c. Orders pending outbound (order_status = PENDING_OUTBOUND, warehouse_status = PACKED)
-- These appear as outbound tasks.

INSERT INTO t_order (id, order_no, user_id, total_price, total_weight_kg, total_volume_m3, trade_mode,
    order_status, warehouse_status, delivery_status, customs_sync_status,
    consignee_name, cabin_no, contact_info,
    ship_no, ship_name, ship_nationality, imo, mmsi, berth_or_anchorage, target_gps,
    shipping_agent_id, shipping_agent_name) VALUES
(2006, 'TH202605290006', 1005,  12.80,  1.590, 0.0015, 'AUTO_TRADE',
    'PENDING_OUTBOUND', 'PACKED', NULL, 'SYNC_SUCCESS',
    '白帆', 'B-201', '13800000005',
    'COSCO-QD-001', '中远青岛号', 'CN', 'IMO9876543', '412345678', 'BERTH-A3', '36.0755,120.3844',
    1002, '港通船舶服务'),
(2007, 'TH202605290007', 1004,  32.40,  3.800, 0.0040, 'AUTO_TRADE',
    'PENDING_OUTBOUND', 'PACKED', NULL, 'SYNC_FAILED',
    'Captain Johnson', 'A-301', '13800000004',
    'MAERSK-001', 'Maersk Elba', 'DK', 'IMO9876544', '412345679', 'ANCHORAGE-B2', '36.0812,120.3901',
    1001, '青岛远洋代理');

-- ============================================================
-- 10. Order Items
-- ============================================================

-- Order 2001: multi-item
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3001, 2001, 'TH202605290001', 1001, 'SKU-COKE-330',    '可口可乐 330ml',  'Coca-Cola 330ml',    2.50, 4, 0.360, 0.0004, 10.00),
(3002, 2001, 'TH202605290001', 1004, 'SKU-BISCUIT-120', '黄油饼干 120g',   'Butter Biscuits 120g', 3.80, 2, 0.130, 0.0002,  7.60),
(3003, 2001, 'TH202605290001', 1005, 'SKU-TEA-500',     '瓶装绿茶 500ml',  'Green Tea 500ml',    3.20, 1, 0.530, 0.0005,  3.20);

-- Order 2002: single-item
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3004, 2002, 'TH202605290002', 1003, 'SKU-COFFEE-200', '即饮咖啡 200ml', 'Ready-to-drink Coffee 200ml', 5.90, 4, 0.220, 0.0003, 23.60);

-- Order 2003: noodles
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3005, 2003, 'TH202605290003', 1006, 'SKU-NOODLE-110', '方便面 110g', 'Instant Noodles 110g', 2.00, 8, 0.120, 0.0002, 16.00);

-- Order 2004: water for review
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3006, 2004, 'TH202605290004', 1002, 'SKU-WATER-550', '矿泉水 550ml', 'Mineral Water 550ml', 1.80, 5, 0.580, 0.0005, 9.00);

-- Order 2005: multi-item for review
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3007, 2005, 'TH202605290005', 1005, 'SKU-TEA-500',    '瓶装绿茶 500ml', 'Green Tea 500ml',    3.20, 4, 0.530, 0.0005, 12.80),
(3008, 2005, 'TH202605290005', 1001, 'SKU-COKE-330',   '可口可乐 330ml',  'Coca-Cola 330ml',    2.50, 2, 0.360, 0.0004,  5.00),
(3009, 2005, 'TH202605290005', 1006, 'SKU-NOODLE-110', '方便面 110g',     'Instant Noodles 110g', 2.00, 1, 0.120, 0.0002,  2.00);

-- Order 2006: outbound, customs OK
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3010, 2006, 'TH202605290006', 1001, 'SKU-COKE-330',  '可口可乐 330ml', 'Coca-Cola 330ml',  2.50, 3, 0.360, 0.0004,  7.50),
(3011, 2006, 'TH202605290006', 1004, 'SKU-BISCUIT-120','黄油饼干 120g',  'Butter Biscuits 120g', 3.80, 1, 0.130, 0.0002, 3.80),
(3012, 2006, 'TH202605290006', 1005, 'SKU-TEA-500',   '瓶装绿茶 500ml', 'Green Tea 500ml',  3.20, 1, 0.530, 0.0005,  3.20);

-- Order 2007: outbound, customs failed
INSERT INTO t_order_item (id, order_id, order_no, product_id, sku_code, product_name_zh, product_name_en, unit_price, quantity, unit_weight_kg, unit_volume_m3, line_amount) VALUES
(3013, 2007, 'TH202605290007', 1002, 'SKU-WATER-550',  '矿泉水 550ml',    'Mineral Water 550ml',  1.80, 6, 0.580, 0.0005, 10.80),
(3014, 2007, 'TH202605290007', 1003, 'SKU-COFFEE-200', '即饮咖啡 200ml',  'Ready-to-drink Coffee 200ml', 5.90, 2, 0.220, 0.0003, 11.80),
(3015, 2007, 'TH202605290007', 1006, 'SKU-NOODLE-110', '方便面 110g',     'Instant Noodles 110g', 2.00, 5, 0.120, 0.0002, 10.00);

-- ============================================================
-- 11. Packages (for outbound orders)
-- ============================================================

INSERT INTO t_package (id, package_no, order_id, order_no, actual_weight_kg, actual_volume_m3, package_status) VALUES
(1001, 'PKG-QD-290006', 2006, 'TH202605290006',  3.800, 0.0028, 'OUTBOUND'),
(1002, 'PKG-QD-290007', 2007, 'TH202605290007',  8.400, 0.0076, 'REVIEWED');

-- ============================================================
-- 12. Drones
-- ============================================================

INSERT INTO t_drone (id, drone_code, model, flight_no, max_payload_kg, max_volume_m3, max_range_km, deliverable_categories, status) VALUES
(1001, 'DRONE-QD-001', 'DJI FlyCart 30', 'FC2026052901', 30.000, 0.200, 15.00, 'Beverages,Food', 'AVAILABLE'),
(1002, 'DRONE-QD-002', 'DJI FlyCart 30', 'FC2026052902', 30.000, 0.200, 15.00, 'Beverages,Food', 'AVAILABLE'),
(1003, 'DRONE-QD-003', 'SkyPort S100',   'SP2026052901', 10.000, 0.100, 10.00, 'Beverages',      'AVAILABLE');
