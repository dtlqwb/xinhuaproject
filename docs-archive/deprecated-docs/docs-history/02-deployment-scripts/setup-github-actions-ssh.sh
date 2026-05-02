#!/bin/bash
set -e

echo "=========================================="
echo "  开始配置GitHub Actions SSH密钥..."
echo "=========================================="
echo ""

# 生成SSH密钥（无密码）
echo "[1/4] 生成SSH密钥对..."
ssh-keygen -t rsa -b 4096 -C "github-actions-deploy" -f ~/.ssh/github_actions -N ''

echo "✓ 密钥生成成功"
echo ""

# 配置authorized_keys
echo "[2/4] 配置authorized_keys..."
cat ~/.ssh/github_actions.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh

echo "✓ authorized_keys配置完成"
echo ""

# 测试本地连接
echo "[3/4] 测试SSH连接..."
if ssh -i ~/.ssh/github_actions -o StrictHostKeyChecking=no -o BatchMode=yes ubuntu@localhost "echo 'Connection successful'" 2>/dev/null; then
    echo "✓ SSH连接测试成功"
else
    echo "⚠️  SSH连接测试失败，但不影响使用"
fi
echo ""

# 显示私钥
echo "[4/4] 私钥内容（请复制以下内容到GitHub Secrets）："
echo ""
echo "=========================================="
echo "  SSH_PRIVATE_KEY"
echo "=========================================="
echo ""
cat ~/.ssh/github_actions
echo ""
echo "=========================================="
echo ""
echo "✅ 配置完成！"
echo ""
echo "下一步操作："
echo "1. 复制上面显示的完整私钥（包括BEGIN和END行）"
echo "2. 访问：https://github.com/dtlqwb/xinhuaproject/settings/secrets/actions"
echo "3. 编辑 SSH_PRIVATE_KEY"
echo "4. 粘贴私钥内容并保存"
echo "5. 重新触发GitHub Actions部署"
echo ""
