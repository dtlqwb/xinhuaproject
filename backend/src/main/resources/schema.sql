-- 创建数据库
CREATE DATABASE IF NOT EXISTS sales_customer_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sales_customer_db;

-- 销售人员表
CREATE TABLE IF NOT EXISTS sales_person (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    department VARCHAR(50) COMMENT '部门',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_phone (phone),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售人员表';

-- 客户信息表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) COMMENT '客户姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    company VARCHAR(100) COMMENT '公司名称',
    position VARCHAR(50) COMMENT '职位',
    industry VARCHAR(50) COMMENT '行业',
    source_type VARCHAR(20) COMMENT '来源类型：voice-语音，photo-拍照，text-文字',
    content TEXT COMMENT '原始内容',
    image_path VARCHAR(500) COMMENT '图片路径',
    audio_text TEXT COMMENT '语音转文字内容',
    requirement TEXT COMMENT '客户需求',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '跟进状态：pending-待跟进，contacted-已联系，negotiating-洽谈中，closed-已成交，lost-已流失',
    sales_id BIGINT NOT NULL COMMENT '销售人员ID',
    attachment_count INT DEFAULT 0 COMMENT '附件数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_sales_id (sales_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (sales_id) REFERENCES sales_person(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

-- 客户附件表
CREATE TABLE IF NOT EXISTS customer_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小（字节）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_customer_id (customer_id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户附件表';

-- 日报表
CREATE TABLE IF NOT EXISTS daily_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    sales_id BIGINT NOT NULL COMMENT '销售ID',
    sales_name VARCHAR(50) COMMENT '销售姓名',
    report_date DATE NOT NULL COMMENT '日报日期',
    today_customers INT DEFAULT 0 COMMENT '今日新增客户数',
    follow_up_customers INT DEFAULT 0 COMMENT '今日跟进客户数',
    signed_customers INT DEFAULT 0 COMMENT '今日签约客户数',
    work_content TEXT COMMENT '工作内容',
    problems TEXT COMMENT '存在问题',
    tomorrow_plan TEXT COMMENT '明日计划',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，submitted-已提交，reviewed-已审核',
    reviewer_id BIGINT COMMENT '审核人ID',
    reviewer_name VARCHAR(50) COMMENT '审核人姓名',
    review_comment TEXT COMMENT '审核意见',
    review_time DATETIME COMMENT '审核时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_sales_id (sales_id),
    INDEX idx_report_date (report_date),
    INDEX idx_status (status),
    UNIQUE KEY uk_sales_date (sales_id, report_date),
    FOREIGN KEY (sales_id) REFERENCES sales_person(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日报表';

-- 营销方案表
CREATE TABLE IF NOT EXISTS marketing_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    plan_name VARCHAR(200) COMMENT '方案名称',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(50) COMMENT '客户姓名',
    sales_id BIGINT NOT NULL COMMENT '销售ID',
    sales_name VARCHAR(50) COMMENT '销售姓名',
    plan_type VARCHAR(20) DEFAULT 'product' COMMENT '方案类型：product-产品方案，service-服务方案，promotion-促销方案',
    plan_content TEXT NOT NULL COMMENT '方案内容',
    ai_generated_content TEXT COMMENT 'AI生成内容',
    execute_date DATE COMMENT '执行时间',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，pending-待执行，executing-执行中，completed-已完成，cancelled-已取消',
    execute_result TEXT COMMENT '执行结果',
    complete_time DATETIME COMMENT '完成时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_customer_id (customer_id),
    INDEX idx_sales_id (sales_id),
    INDEX idx_status (status),
    INDEX idx_execute_date (execute_date),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销方案表';

-- 管理员表
CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    role VARCHAR(20) DEFAULT 'admin' COMMENT '角色：admin-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员账号（密码：admin123，实际使用时需要加密）
INSERT INTO admin_user (username, password, role) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin');

-- 插入测试销售人员账号（密码：123456，实际使用时需要加密）
INSERT INTO sales_person (name, phone, password, department) VALUES 
('张三', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售部'),
('李四', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售部');
