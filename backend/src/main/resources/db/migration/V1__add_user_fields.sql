-- 扩展 admin_user 表字段
ALTER TABLE admin_user 
ADD COLUMN phone VARCHAR(20) COMMENT '手机号' AFTER password,
ADD COLUMN email VARCHAR(100) COMMENT '邮箱' AFTER phone,
ADD COLUMN real_name VARCHAR(50) COMMENT '真实姓名' AFTER email;

-- 更新角色注释
ALTER TABLE admin_user MODIFY COLUMN role VARCHAR(20) DEFAULT 'admin' COMMENT '角色：super_admin-超级管理员，admin-普通管理员';

-- 更新现有 admin 账号为超级管理员
UPDATE admin_user SET role = 'super_admin', real_name = '系统管理员' WHERE username = 'admin';
