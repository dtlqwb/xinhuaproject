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
    sales_id BIGINT NOT NULL COMMENT '销售人员ID',
    report_date DATE NOT NULL COMMENT '日报日期',
    summary TEXT COMMENT '总结内容',
    customer_count INT DEFAULT 0 COMMENT '客户数量',
    ai_analysis TEXT COMMENT 'AI分析结果',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_sales_id (sales_id),
    INDEX idx_report_date (report_date),
    UNIQUE KEY uk_sales_date (sales_id, report_date),
    FOREIGN KEY (sales_id) REFERENCES sales_person(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日报表';

-- 营销方案表
CREATE TABLE IF NOT EXISTS marketing_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    plan_content TEXT NOT NULL COMMENT '方案内容',
    priority VARCHAR(20) DEFAULT 'medium' COMMENT '优先级：high-高，medium-中，low-低',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '执行状态：pending-待执行，executing-执行中，completed-已完成',
    generate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    execute_time DATETIME COMMENT '执行时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_customer_id (customer_id),
    INDEX idx_priority (priority),
    INDEX idx_status (status),
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
