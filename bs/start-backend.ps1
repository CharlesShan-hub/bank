# ============================================================
# 后端启动脚本：Spring Boot（端口 6997）
# 用法：在 bs 目录下执行  .\start-backend.ps1
# 首次运行会自动下载 Maven 依赖、建库建表并预置演示数据
# 依赖：JDK 17 与 Maven（需已加入 PATH）
# ============================================================

$ErrorActionPreference = "Stop"

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "未检测到 Maven（mvn），请先安装 JDK 17 与 Maven 并加入 PATH" -ForegroundColor Red
    exit 1
}

Push-Location "$PSScriptRoot\backend"
mvn spring-boot:run
Pop-Location
