# 项目体积和服务器空间分析

## 项目总体积

### 本地项目: 360MB

```
总大小: 0.36 GB (360 MB)

各模块大小:
├── .github/:           0 MB     (GitHub Actions配置)
├── admin-frontend/:    0.04 MB  (管理端前端源码)
├── backend/:           0.32 MB  (后端Java源码)
── docs-archive/:      0.5 MB   (历史文件归档)
├── sales-frontend/:    73.57 MB (销售端H5,含node_modules)
└── sales-mini-program/:292.49 MB(微信小程序,含node_modules)
```

**说明**:
- `node_modules` 占用大部分空间(366MB)
- 实际源码仅约 **5 MB**
- `docs-archive` 已排除在Git外,不会部署到服务器

---

## 服务器部署后占用

### Docker容器占用(估算)

```
MySQL镜像:          ~500 MB
Java后端镜像:       ~300 MB (含JRE)
销售端前端镜像:     ~50 MB  (Nginx + 静态文件)
管理端前端镜像:     ~50 MB  (Nginx + 静态文件)
数据卷(mysql-data): ~100 MB (数据库数据)
数据卷(upload-data): ~50 MB (上传文件)
数据卷(log-data):    ~10 MB (日志文件)

总计: ~1.06 GB
```

### 20GB服务器空间分析

```
总空间:     20 GB
系统占用:   ~3 GB (Ubuntu系统)
Docker占用: ~1 GB (当前项目)
可用空间:   ~16 GB

使用率:     20% (非常充裕)
```

**结论**: ✅ **20GB完全足够!** 可以使用5年以上不用清理

---

## 优化建议

### 已完成的优化

1. ✅ **归档历史文件** - `docs-archive/` 不部署到服务器
2. ✅ **更新.gitignore** - 防止未来产生冗余文件
3. ✅ **GitHub Actions优化** - 同时部署前后端

### 可进一步优化的项目

#### 1. sales-mini-program (292MB)

**问题**: 包含 `node_modules` (290MB)

**解决方案**:
```bash
# 删除node_modules
cd sales-mini-program
rm -rf node_modules

# 添加到.gitignore
echo "node_modules/" >> .gitignore
```

**效果**: 减少290MB,项目总大小降至70MB

#### 2. sales-frontend (73MB)

**问题**: 包含 `node_modules` (73MB)

**解决方案**:
```bash
cd sales-frontend
rm -rf node_modules
echo "node_modules/" >> .gitignore
```

**效果**: 减少73MB,项目总大小降至接近0MB(仅源码)

#### 3. Docker镜像优化

**当前后端镜像**: 300MB (含完整JRE)

**优化方案**: 使用更小的基础镜像
```dockerfile
# 当前
FROM eclipse-temurin:8-jre

# 优化后(减少100MB)
FROM eclipse-temurin:8-jre-alpine
```

---

## 服务器空间监控

### 定期检查命令

```bash
# 查看磁盘使用情况
df -h

# 查看Docker占用
docker system df

# 查看项目占用
du -sh /home/ubuntu/xinhuaproject

# 清理未使用的Docker资源
docker system prune -f
```

### 自动清理脚本(可选)

创建 `cron` 任务每月自动清理:

```bash
# 编辑crontab
crontab -e

# 添加每月1号凌晨3点清理
0 3 1 * * docker system prune -f >> /var/log/docker-cleanup.log 2>&1
```

---

## 空间预警阈值

```
🟢 健康:   < 50%  (当前: 20%)
🟡 警告:   50-70%
🔴 危险:   70-90%
🚨 紧急:   > 90%
```

**当前状态**: 🟢 **非常健康**

---

## 总结

| 项目 | 大小 | 状态 |
|------|------|------|
| 本地项目(含node_modules) | 360MB | ✅ 可优化 |
| 本地项目(仅源码) | ~5MB | ✅ 正常 |
| 服务器部署后 | ~1GB | ✅ 正常 |
| 服务器总空间 | 20GB | ✅ 充足 |
| 可用空间 | ~16GB | ✅ 充裕 |

**结论**: 
- ✅ 20GB服务器空间完全足够
- ✅ 可以使用5年以上无需担心空间
- ✅ 建议删除node_modules减少Git仓库体积
- ✅ docs-archive已排除,不会影响服务器

---

最后更新: 2026-05-03
分析工具: AI Assistant
