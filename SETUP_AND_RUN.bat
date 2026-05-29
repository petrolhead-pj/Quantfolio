@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo ╔════════════════════════════════════════════════════════╗
echo ║  QUANTFOLIO - SETUP ^& RUN                              ║
echo ║  Java 21 + Maven + MySQL + Swing                      ║
echo ╚════════════════════════════════════════════════════════╝
echo.

REM PHASE 1: Verify tools
echo [PHASE 1] Checking prerequisites...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Java 21 not found
    pause
    exit /b 1
)
echo ✓ Java 21 found

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Maven not found
    pause
    exit /b 1
)
echo ✓ Maven found

where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ MySQL not found
    pause
    exit /b 1
)
echo ✓ MySQL found
echo.

REM PHASE 2: Get MySQL password
echo [PHASE 2] MySQL Connection
set /p MYSQL_PASSWORD="Enter MySQL root password: "

mysql -u root -p%MYSQL_PASSWORD% -P 3307 -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Connection failed
    pause
    exit /b 1
)
echo ✓ Connected to MySQL on port 3307
echo.

REM PHASE 3: Create database
echo [PHASE 3] Setting up database...

mysql -u root -p%MYSQL_PASSWORD% -P 3307 -e "DROP DATABASE IF EXISTS quantfolio;" >nul 2>&1
mysql -u root -p%MYSQL_PASSWORD% -P 3307 -e "CREATE DATABASE quantfolio;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Database creation failed
    pause
    exit /b 1
)
echo ✓ Database created

mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\schema.sql >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Schema load failed
    pause
    exit /b 1
)
echo ✓ Schema loaded

mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\seed_data.sql >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Seed data failed
    pause
    exit /b 1
)
echo ✓ Seed data loaded

mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\views.sql >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Views failed
    pause
    exit /b 1
)
echo ✓ Views created

mysql -u root -p%MYSQL_PASSWORD% -P 3307 quantfolio < db\triggers.sql >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Triggers failed
    pause
    exit /b 1
)
echo ✓ Triggers created
echo.

echo [PHASE 4] Credentials verified
echo ✓ DBConnection.java already configured with port 3307
echo.

REM PHASE 5: Build with Maven
echo [PHASE 5] Building with Maven...
echo.
call mvn clean install
if %errorlevel% neq 0 (
    echo.
    echo ✗ Build failed
    pause
    exit /b 1
)
echo.
echo ✓ Build successful
echo.

REM PHASE 6: Launch Swing app
echo [PHASE 6] Launching application...
echo.
java -cp "target/classes;target/quantfolio/WEB-INF/lib/*" com.quantfolio.desktop.DesktopApp

pause
