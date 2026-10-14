-- ============================================
-- 苍穹外卖数据库建表脚本
-- 包含原始表 + 扩展表（优惠券、评价、支付流水）
-- dish表已增加 stock（库存）、version（乐观锁版本号）字段
-- ============================================

CREATE DATABASE IF NOT EXISTS sky_take_out DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE sky_take_out;

-- ============================================
-- 1. 员工表
-- ============================================
DROP TABLE IF EXISTS employee;
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(32) NOT NULL COMMENT '姓名',
    username VARCHAR(32) NOT NULL COMMENT '用户名',
    password VARCHAR(64) NOT NULL COMMENT '密码',
    phone VARCHAR(11) NOT NULL COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别',
    id_number VARCHAR(18) COMMENT '身份证号',
    status INT DEFAULT 1 COMMENT '账号状态 0禁用 1正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    UNIQUE KEY idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 默认管理员账号 admin/123456（MD5加密）
INSERT INTO employee (name, username, password, phone, status, create_time, update_time)
VALUES ('管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', 1, NOW(), NOW());

-- ============================================
-- 2. 分类表
-- ============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    type INT NOT NULL COMMENT '类型 1菜品分类 2套餐分类',
    name VARCHAR(32) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '顺序',
    status INT DEFAULT 1 COMMENT '分类状态 0禁用 1启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    UNIQUE KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ============================================
-- 3. 菜品表（已增加 stock 库存、version 乐观锁版本号）
-- ============================================
DROP TABLE IF EXISTS dish;
CREATE TABLE dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(32) NOT NULL COMMENT '菜品名称',
    category_id BIGINT NOT NULL COMMENT '分类id',
    price DECIMAL(10,2) NOT NULL COMMENT '菜品价格',
    image VARCHAR(255) NOT NULL COMMENT '图片',
    description VARCHAR(255) COMMENT '描述信息',
    status INT DEFAULT 1 COMMENT '售卖状态 0停售 1起售',
    stock INT DEFAULT 999 COMMENT '库存数量（-1表示不限量）',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    KEY idx_category_id (category_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- ============================================
-- 4. 菜品口味表
-- ============================================
DROP TABLE IF EXISTS dish_flavor;
CREATE TABLE dish_flavor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    dish_id BIGINT NOT NULL COMMENT '菜品',
    name VARCHAR(32) NOT NULL COMMENT '口味名称',
    value VARCHAR(255) COMMENT '口味数据list（JSON）',
    KEY idx_dish_id (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品口味表';

-- ============================================
-- 5. 套餐表
-- ============================================
DROP TABLE IF EXISTS setmeal;
CREATE TABLE setmeal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT NOT NULL COMMENT '菜品分类id',
    name VARCHAR(32) NOT NULL COMMENT '套餐名称',
    price DECIMAL(10,2) NOT NULL COMMENT '套餐价格',
    status INT DEFAULT 1 COMMENT '售卖状态 0停售 1起售',
    description VARCHAR(255) COMMENT '描述信息',
    image VARCHAR(255) NOT NULL COMMENT '图片',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐表';

-- ============================================
-- 6. 套餐菜品关系表
-- ============================================
DROP TABLE IF EXISTS setmeal_dish;
CREATE TABLE setmeal_dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    setmeal_id BIGINT NOT NULL COMMENT '套餐id',
    dish_id BIGINT NOT NULL COMMENT '菜品id',
    name VARCHAR(32) COMMENT '菜品名称（冗余）',
    price DECIMAL(10,2) COMMENT '菜品单价（冗余）',
    copies INT DEFAULT 1 COMMENT '份数',
    KEY idx_setmeal_id (setmeal_id),
    KEY idx_dish_id (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐菜品关系表';

-- ============================================
-- 7. 用户表（微信用户）
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    openid VARCHAR(45) COMMENT '微信openid',
    name VARCHAR(32) COMMENT '姓名',
    phone VARCHAR(11) COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别',
    id_number VARCHAR(18) COMMENT '身份证号',
    avatar VARCHAR(500) COMMENT '头像',
    create_time DATETIME COMMENT '注册时间',
    UNIQUE KEY idx_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 8. 地址簿表
-- ============================================
DROP TABLE IF EXISTS address_book;
CREATE TABLE address_book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户id',
    consignee VARCHAR(50) NOT NULL COMMENT '收货人',
    sex VARCHAR(2) COMMENT '性别',
    phone VARCHAR(11) NOT NULL COMMENT '手机号',
    province_code VARCHAR(12) COMMENT '省级区划编号',
    province_name VARCHAR(32) COMMENT '省级名称',
    city_code VARCHAR(12) COMMENT '市级区划编号',
    city_name VARCHAR(32) COMMENT '市级名称',
    district_code VARCHAR(12) COMMENT '区级区划编号',
    district_name VARCHAR(32) COMMENT '区级名称',
    detail VARCHAR(200) COMMENT '详细地址',
    label VARCHAR(100) COMMENT '标签',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认 0否 1是',
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址簿表';

-- ============================================
-- 9. 购物车表
-- ============================================
DROP TABLE IF EXISTS shopping_cart;
CREATE TABLE shopping_cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(32) NOT NULL COMMENT '商品名称',
    user_id BIGINT NOT NULL COMMENT '用户id',
    dish_id BIGINT COMMENT '菜品id',
    setmeal_id BIGINT COMMENT '套餐id',
    dish_flavor VARCHAR(50) COMMENT '口味',
    number INT DEFAULT 1 COMMENT '数量',
    amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    image VARCHAR(255) COMMENT '图片',
    create_time DATETIME COMMENT '创建时间',
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================
-- 10. 订单表
-- ============================================
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    number VARCHAR(50) NOT NULL COMMENT '订单号',
    status INT NOT NULL DEFAULT 1 COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消',
    user_id BIGINT NOT NULL COMMENT '下单用户',
    address_book_id BIGINT NOT NULL COMMENT '地址id',
    order_time DATETIME NOT NULL COMMENT '下单时间',
    checkout_time DATETIME COMMENT '结账时间',
    pay_method TINYINT DEFAULT 1 COMMENT '支付方式 1微信 2支付宝',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0未支付 1已支付 2退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '实收金额',
    remark VARCHAR(100) COMMENT '备注',
    phone VARCHAR(11) COMMENT '手机号',
    address VARCHAR(255) COMMENT '详细地址',
    consignee VARCHAR(32) COMMENT '收货人',
    cancel_reason VARCHAR(255) COMMENT '订单取消原因',
    rejection_reason VARCHAR(255) COMMENT '拒单原因',
    cancel_time DATETIME COMMENT '订单取消时间',
    estimated_delivery_time DATETIME COMMENT '预计送达时间',
    delivery_status TINYINT DEFAULT 1 COMMENT '配送状态 1立即送出 0选择具体时间',
    delivery_time DATETIME COMMENT '送达时间',
    pack_amount INT DEFAULT 0 COMMENT '打包费',
    tableware_number INT COMMENT '餐具数量',
    tableware_status TINYINT DEFAULT 1 COMMENT '餐具数量状态 1按餐量提供 0选择具体数量',
    coupon_id BIGINT COMMENT '优惠券id',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    refund_amount DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    UNIQUE KEY idx_number (number),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_order_time (order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================
-- 11. 订单明细表
-- ============================================
DROP TABLE IF EXISTS order_detail;
CREATE TABLE order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(32) NOT NULL COMMENT '商品名称',
    order_id BIGINT NOT NULL COMMENT '订单id',
    dish_id BIGINT COMMENT '菜品id',
    setmeal_id BIGINT COMMENT '套餐id',
    dish_flavor VARCHAR(50) COMMENT '口味',
    number INT NOT NULL DEFAULT 1 COMMENT '数量',
    amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    image VARCHAR(255) COMMENT '图片',
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================
-- 12. 支付流水表（扩展）
-- ============================================
DROP TABLE IF EXISTS payment_record;
CREATE TABLE payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    order_id BIGINT NOT NULL COMMENT '订单id',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_method TINYINT DEFAULT 1 COMMENT '支付方式 1微信 2支付宝',
    pay_time DATETIME COMMENT '支付时间',
    transaction_id VARCHAR(64) COMMENT '第三方支付流水号',
    status TINYINT DEFAULT 1 COMMENT '状态 1支付成功 2退款',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_order_id (order_id),
    KEY idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';

-- ============================================
-- 13. 优惠券表（扩展）
-- ============================================
DROP TABLE IF EXISTS coupon;
CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(64) NOT NULL COMMENT '优惠券名称',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型 1满减券 2折扣券',
    discount_amount DECIMAL(10,2) COMMENT '优惠金额（满减券）',
    discount_rate DECIMAL(3,2) COMMENT '折扣率（折扣券，如0.8表示8折）',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    total_count INT DEFAULT 0 COMMENT '发放总量（0表示不限）',
    used_count INT DEFAULT 0 COMMENT '已使用数量',
    start_time DATETIME COMMENT '生效开始时间',
    end_time DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- ============================================
-- 14. 用户优惠券表（扩展）
-- ============================================
DROP TABLE IF EXISTS user_coupon;
CREATE TABLE user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户id',
    coupon_id BIGINT NOT NULL COMMENT '优惠券id',
    status TINYINT DEFAULT 0 COMMENT '状态 0未使用 1已使用 2已过期',
    order_id BIGINT COMMENT '使用的订单id',
    used_time DATETIME COMMENT '使用时间',
    receive_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    KEY idx_user_id (user_id),
    KEY idx_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ============================================
-- 15. 评价表（扩展）
-- ============================================
DROP TABLE IF EXISTS review;
CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户id',
    order_id BIGINT NOT NULL COMMENT '订单id',
    rating TINYINT NOT NULL DEFAULT 5 COMMENT '评分 1-5星',
    content VARCHAR(500) COMMENT '评价内容',
    images VARCHAR(1000) COMMENT '评价图片（JSON数组）',
    reply VARCHAR(500) COMMENT '商家回复',
    reply_time DATETIME COMMENT '回复时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    KEY idx_user_id (user_id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ============================================
-- 初始化测试数据
-- ============================================

-- 分类数据
INSERT INTO category (type, name, sort, status, create_time, update_time) VALUES
(1, '热门菜品', 1, 1, NOW(), NOW()),
(1, '主食', 2, 1, NOW(), NOW()),
(1, '小吃', 3, 1, NOW(), NOW()),
(1, '饮品', 4, 1, NOW(), NOW()),
(2, '单人套餐', 1, 1, NOW(), NOW()),
(2, '双人套餐', 2, 1, NOW(), NOW());

-- 菜品数据
INSERT INTO dish (name, category_id, price, image, description, status, stock, create_time, update_time) VALUES
('宫保鸡丁', 2, 28.00, '/images/dish/gongbaojiding.jpg', '经典川菜，鸡肉鲜嫩，花生酥脆', 1, 100, NOW(), NOW()),
('鱼香肉丝', 2, 26.00, '/images/dish/yuxiangrousi.jpg', '酸甜可口，下饭神器', 1, 100, NOW(), NOW()),
('麻婆豆腐', 2, 18.00, '/images/dish/mapotofu.jpg', '麻辣鲜香，豆腐嫩滑', 1, 100, NOW(), NOW()),
('红烧肉', 1, 38.00, '/images/dish/hongshaorou.jpg', '肥而不腻，入口即化', 1, 50, NOW(), NOW()),
('糖醋里脊', 1, 32.00, '/images/dish/tangculiji.jpg', '外酥里嫩，酸甜可口', 1, 50, NOW(), NOW()),
('米饭', 2, 2.00, '/images/dish/rice.jpg', '东北大米，香软可口', 1, 999, NOW(), NOW()),
('可乐', 4, 5.00, '/images/dish/cola.jpg', '冰镇可乐', 1, 200, NOW(), NOW()),
('酸梅汤', 4, 8.00, '/images/dish/suanmeitang.jpg', '自制酸梅汤，解暑开胃', 1, 100, NOW(), NOW());

-- 套餐数据
INSERT INTO setmeal (category_id, name, price, status, description, image, create_time, update_time) VALUES
(5, '单人工作餐A', 35.00, 1, '宫保鸡丁+米饭+可乐', '/images/setmeal/setmeal1.jpg', NOW(), NOW()),
(5, '单人工作餐B', 32.00, 1, '鱼香肉丝+米饭+酸梅汤', '/images/setmeal/setmeal2.jpg', NOW(), NOW()),
(6, '双人欢聚餐', 88.00, 1, '红烧肉+糖醋里脊+麻婆豆腐+米饭2份+可乐2份', '/images/setmeal/setmeal3.jpg', NOW(), NOW());

-- 套餐菜品关联
INSERT INTO setmeal_dish (setmeal_id, dish_id, name, price, copies) VALUES
(1, 1, '宫保鸡丁', 28.00, 1),
(1, 6, '米饭', 2.00, 1),
(1, 7, '可乐', 5.00, 1),
(2, 2, '鱼香肉丝', 26.00, 1),
(2, 6, '米饭', 2.00, 1),
(2, 8, '酸梅汤', 8.00, 1),
(3, 4, '红烧肉', 38.00, 1),
(3, 5, '糖醋里脊', 32.00, 1),
(3, 3, '麻婆豆腐', 18.00, 1),
(3, 6, '米饭', 2.00, 2),
(3, 7, '可乐', 5.00, 2);

-- 优惠券数据
INSERT INTO coupon (name, type, discount_amount, min_amount, total_count, start_time, end_time, status) VALUES
('新人专享5元券', 1, 5.00, 20.00, 1000, '2025-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('满50减10', 1, 10.00, 50.00, 500, '2025-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('满100减25', 1, 25.00, 100.00, 200, '2025-01-01 00:00:00', '2026-12-31 23:59:59', 1);
