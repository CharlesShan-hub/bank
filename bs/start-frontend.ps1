# ============================================================
# 前端启动脚本：启动 Vite 开发服务器（默认端口 7997）
# 用法：在 bs 目录下执行  .\start-frontend.ps1
# 首次使用请先运行 setup-env.ps1 完成环境配置
# ============================================================

$ErrorActionPreference = "Stop"

if (-not (Get-Command yarn -ErrorAction SilentlyContinue)) {
    Write-Host "未检测到 yarn，请先运行 .\setup-env.ps1 完成环境配置" -ForegroundColor Red
    exit 1
}

Push-Location "$PSScriptRoot\frontend"
yarn dev
Pop-Location
