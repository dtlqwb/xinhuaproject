# 历史文档归档

本目录包含了项目开发过程中的临时文档、修复脚本和过时的指南。

## 📁 目录结构

### 01-deployment-fixes/
部署相关的修复记录和临时总结文档
- 项目总结文档
- 部署问题修复记录

### 02-deployment-scripts/
各种部署脚本(已废弃,仅供参考)
- Python 部署脚本 (*.py)
- Shell 部署脚本 (*.sh)
- Batch 部署脚本 (*.bat)
- PowerShell 部署脚本 (*.ps1)

### 03-troubleshooting/
问题排查和修复文档
- 数据库连接修复
- Docker 配置修复
- MySQL 认证修复
- POM 依赖修复
- 管理后台网络问题修复
- 登录问题修复

### 04-guides-archive/
过时的指南文档
- 后端启动指南
- Maven 安装指南
- 部署指南
- Git 部署指南
- GitHub Actions 配置指南
- SSH 设置指南
- 测试指南
- 部署检查清单

## ⚠️ 注意

**这些文档仅供参考,可能已过时!**

当前有效的文档:
- ✅ [README.md](../README.md) - 项目主文档
- ✅ [.env.example](../.env.example) - 环境配置示例
- ✅ [docker-compose.yml](../docker-compose.yml) - Docker 编排配置

## 📝 整理时间

2026-04-25

## 🔄 如何恢复

如果需要使用某个历史文档,可以从此目录复制回项目根目录:

```bash
# 示例:恢复某个部署脚本
cp docs-history/02-deployment-scripts/deploy-now.py ./
```
