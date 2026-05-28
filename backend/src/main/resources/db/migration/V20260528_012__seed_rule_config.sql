-- V20260528_012: Seed rule config data

INSERT INTO t_rule_config (id, rule_key, rule_name_zh, rule_name_en, rule_value, description, status) VALUES
(1, 'MATCHING_ORDER_THRESHOLD_AMOUNT', '匹配订单最低金额', 'Matching Order Min Amount', '500.00', '匹配订单模式最低金额阈值', 'ENABLED'),
(2, 'MATCHING_ORDER_THRESHOLD_WEIGHT', '匹配订单最低重量(kg)', 'Matching Order Min Weight', '50.000', '匹配订单模式最低重量阈值', 'ENABLED'),
(3, 'DRONE_DELIVERY_MAX_RANGE_KM', '无人机配送最大距离(km)', 'Drone Delivery Max Range', '15.00', '无人机配送最大距离限制', 'ENABLED'),
(4, 'CUSTOMS_SYNC_MAX_RETRIES', '海关同步最大重试次数', 'Customs Sync Max Retries', '3', '海关同步最大重试次数', 'ENABLED');
