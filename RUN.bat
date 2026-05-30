@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo ╔════════════════════════════════════════════════════════╗
echo ║  QUANTFOLIO - SETUP ^& RUN                              ║
echo ╚════════════════════════════════════════════════════════╝
echo.

echo [1/7] Verifying prerequisites...
where java >nul 2>&1 || (echo Java not found && pause && exit /b 1)
where mvn >nul 2>&1 || (echo Maven not found && pause && exit /b 1)
where mysql >nul 2>&1 || (echo MySQL not found && pause && exit /b 1)
echo ✓ All tools found
echo.

echo [2/7] Testing MySQL connection...
set /p MYSQL_PASSWORD="Enter MySQL root password: "
mysql -u root -p%MYSQL_PASSWORD% -P 3307 -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ MySQL connection failed - incorrect password
    pause
    exit /b 1
)
echo ✓ MySQL connected
echo.

echo [3/7] Creating database...
mysql -u root -p%MYSQL_PASSWORD% -P 3307 -e "DROP DATABASE IF EXISTS quantfolio;"
mysql -u root -p%MYSQL_PASSWORD% -P 3307 -e "CREATE DATABASE quantfolio;"
echo ✓ Database created
echo.

echo [4/7] Loading schema...
mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\schema.sql
mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\seed_data.sql
mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\views.sql
mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\triggers.sql
echo ✓ Schema loaded
echo.

echo [5/7] Building with Maven...
call mvn clean install -q
if %errorlevel% neq 0 (
    echo ✗ Build failed
    pause
    exit /b 1
)
echo ✓ Build successful
echo.

echo [6/7] Launching application...
echo ✓ Starting Swing app...
echo.
java -cp "target/classes;target/quantfolio/WEB-INF/lib/*" com.quantfolio.desktop.DesktopApp

echo.
echo ✓ Application closed
pause
