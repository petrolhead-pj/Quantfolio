@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo Setting up Quantfolio...
echo.

set PASS=VedantMyRootSQL@242

echo [1/4] Creating database...
mysql -u root -p%PASS% -P 3307 -e "DROP DATABASE IF EXISTS quantfolio;" >nul 2>&1
mysql -u root -p%PASS% -P 3307 -e "CREATE DATABASE quantfolio;" >nul 2>&1

echo [2/4] Loading schema...
mysql -u root -p%PASS% -P 3307 quantfolio < db\schema.sql >nul 2>&1
mysql -u root -p%PASS% -P 3307 quantfolio < db\seed_data.sql >nul 2>&1
mysql -u root -p%PASS% -P 3307 quantfolio < db\views.sql >nul 2>&1
mysql -u root -p%PASS% -P 3307 quantfolio < db\triggers.sql >nul 2>&1

echo [3/4] Database ready
echo.
echo [4/4] Launching Quantfolio...
echo.

java -cp "target/classes;target/quantfolio/WEB-INF/lib/*" com.quantfolio.desktop.DesktopApp

echo.
echo Application closed
pause
