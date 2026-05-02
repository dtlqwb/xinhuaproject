# 项目历史文件归档

本目录存放项目开发过程中产生的临时文件、测试脚本和历史文档。

## 目录结构

### 📁 scripts/ - 临时调试脚本
项目开发阶段使用的Python和PowerShell调试脚本,现已废弃。

**包含内容**:
- 容器状态检查脚本
- 日志诊断脚本  
- 部署修复脚本
- API连接测试脚本

**状态**:  已废弃,不建议使用

---

### 📁 test-files/ - 过时测试脚本
项目早期使用的测试和验证脚本。

**包含内容**:
- 系统健康检查脚本
- API测试脚本
- 部署验证脚本
- AI解析修复脚本

**状态**: ❌ 已废弃,建议使用GitHub Actions自动化测试

---

### 📁 documents/ - 历史文档
项目开发过程中产生的各类文档和报告。

**包含内容**:
- AI配置指南 (AI_CONFIG_GUIDE.md)
- 阿里云集成文档 (ALIYUN_AI_INTEGRATION.md)
- 部署指南 (DEPLOYMENT_GUIDE.md等)
- 测试报告和清单
- 功能验收文档
- 故障修复记录

**状态**: ⚠️ 历史参考,可能已过时

---

### 📁 deprecated-docs/ - 废弃代码和文档
包含已废弃的后端测试代码和更早期的文档历史。

**包含内容**:
- backend-test-code/ - 后端单元测试代码(编译失败,已废弃)
- docs-history/ - 更早期的文档归档

**状态**: ❌ 已废弃,仅保留历史参考

---

## 使用说明

### 何时需要查看归档文件?

1. **历史问题排查**: 如果当前代码出现类似的历史问题,可参考修复记录
2. **配置参考**: 某些AI配置、部署配置可能仍有参考价值
3. **学习目的**: 了解项目发展历程和技术演进

### 如何恢复归档文件?

如果需要恢复某个归档文件到主目录:

```bash
# 从归档恢复到主目录
cp docs-archive/documents/DEPLOYMENT_GUIDE.md ./DEPLOYMENT_GUIDE.md

# 或者移动
mv docs-archive/scripts/deploy-fix.ps1 ./deploy-fix.ps1
```

---

## 清理建议

### 当前主目录(清爽)

```
xinhuaproject/
├── .github/              # GitHub Actions配置
├── admin-frontend/       # 管理端前端
├── backend/              # Java后端
├── sales-frontend/       # 销售端前端
├── sales-mini-program/   # 微信小程序
├── docs-archive/         # 历史文件归档
├── .env.example          # 环境变量示例
├── .gitignore            # Git忽略配置
├── deploy-backend.ps1    # 后端部署脚本
── docker-compose.yml    # Docker编排
└── README.md             # 项目主文档
```

### 文件统计

- **主目录文件**: 5个文件 + 5个目录 = **清爽**
- **归档目录文件**: 60+个历史文件
- **清理比例**: 约90%的冗余文件已归档

---

## 维护规则

### ✅ 应该保留在主目录
- 核心源代码(admin-frontend, backend, sales-frontend)
- Docker配置文件(docker-compose.yml)
- 部署脚本(如果经常使用)
- 项目主文档(README.md)
- 配置文件(.env.example, .gitignore)
- CI/CD配置(.github/)

###  应该归档或删除
- 一次性调试脚本
- 历史修复记录
- 过时的测试报告
- 临时诊断工具
- 重复的部署指南
- 废弃的测试代码

### 📝 文档管理最佳实践

1. **README.md**: 只保留最新、最核心的项目说明
2. **docs/**: 如有必要,只保留1-2个核心文档(如API文档、部署指南)
3. **历史记录**: 所有临时文档统一归档到docs-archive/
4. **定期清理**: 每3个月审查一次归档文件,删除完全无用的内容

---

## 下次清理计划

建议3个月后再次审查本归档目录,删除以下内容:

- [ ] 所有Python调试脚本(99%不会再使用)
- [ ] 所有PowerShell诊断脚本
- [ ] 所有测试报告(已有GitHub Actions)
- [ ] 历史部署指南(已有自动化部署)
- [ ] 废弃的测试代码

**预计可删除**: 约80%的归档文件

---

最后更新: 2026-05-03
归档操作者: AI Assistant
归档原因: 项目主目录文件过多(50+个),严重影响可读性和维护性
