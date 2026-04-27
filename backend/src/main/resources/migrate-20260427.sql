-- 数据库迁移脚本：更新日报表和营销方案表结构
-- 执行日期：2026-04-27

USE sales_customer_db;

-- 备份原表数据（如果需要）
-- CREATE TABLE daily_report_backup AS SELECT * FROM daily_report;
-- CREATE TABLE marketing_plan_backup AS SELECT * FROM marketing_plan;

-- 删除旧表（如果存在数据请先备份）
DROP TABLE IF EXISTS daily_report;
DROP TABLE IF EXISTS marketing_plan;

-- 重新创建日报表
CREATE TABLE daily_report (
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

-- 重新创建营销方案表
CREATE TABLE marketing_plan (
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

-- 验证表结构
SHOW CREATE TABLE daily_report;
SHOW CREATE TABLE marketing_plan;
