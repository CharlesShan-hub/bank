# ============================================================
# 环境配置脚本：安装 scoop -> 通过 scoop 安装 node / yarn -> 安装前端依赖
# 用法：在 bs 目录下执行  .\setup-env.ps1
# 说明：后端另需 JDK 17 与 Maven，本脚本不处理
# ============================================================

$ErrorActionPreference = "Stop"

# ---------- 1. 安装 scoop（已装则跳过） ----------
$scoopShims = "$env:USERPROFILE\scoop\shims"
if (-not (Get-Command scoop -ErrorAction SilentlyContinue) -and -not (Test-Path $scoopShims)) {
    Write-Host "== 未检测到 scoop，开始安装 ==" -ForegroundColor Cyan
    # 允许当前用户运行本地脚本及远程安装脚本
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
    Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression
}
else {
    Write-Host "== scoop 已安装，跳过 ==" -ForegroundColor Green
}

# scoop 刚装完时当前会话可能还没包含其 PATH，这里手动刷新一次
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" +
            [System.Environment]::GetEnvironmentVariable("Path", "User")

# 统一的 scoop 调用入口：优先用命令，找不到就退回 shim 脚本路径（覆盖刚装完还没刷新 profile 的场景）
$scoopCmd = if (Get-Command scoop -ErrorAction SilentlyContinue) { "scoop" } else { "$scoopShims\scoop.ps1" }

# ---------- 2. 通过 scoop 安装 node 与 yarn（已装则跳过） ----------
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "== 通过 scoop 安装 nodejs ==" -ForegroundColor Cyan
    & $scoopCmd install nodejs
}
else {
    Write-Host "== node 已安装（$(node -v)），跳过 ==" -ForegroundColor Green
}

if (-not (Get-Command yarn -ErrorAction SilentlyContinue)) {
    Write-Host "== 通过 scoop 安装 yarn ==" -ForegroundColor Cyan
    & $scoopCmd install yarn
}
else {
    Write-Host "== yarn 已安装（$(yarn -v)），跳过 ==" -ForegroundColor Green
}

# ---------- 3. 安装前端依赖 ----------
Write-Host "== 安装前端依赖（yarn install）==" -ForegroundColor Cyan
Push-Location "$PSScriptRoot\frontend"
yarn install
Pop-Location

Write-Host "== 环境配置完成，可运行 start-backend.ps1 与 start-frontend.ps1 启动系统 ==" -ForegroundColor Green
