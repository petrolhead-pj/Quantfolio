@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo ╔════════════════════════════════════════════════════════╗
echo ║  QUANTFOLIO - SETUP ^& RUN                              ║
echo ║  Java 17 + Maven + MySQL + Swing                      ║
echo ╚════════════════════════════════════════════════════════╝
echo.

REM PHASE 1: Verify tools
echo [PHASE 1] Checking prerequisites...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo X Java not found
    pause
    exit /b 1
)
echo OK Java found

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo X Maven not found
    pause
    exit /b 1
)
echo OK Maven found

where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo X MySQL not found
    pause
    exit /b 1
)
echo OK MySQL found
echo.

REM PHASE 2: Get MySQL password
echo [PHASE 2] MySQL Connection
set /p MYSQL_PASSWORD="Enter MySQL root password: "

mysql -u root -p%MYSQL_PASSWORD% -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo X Connection failed. Check your password.
    pause
    exit /b 1
)
echo OK Connected to MySQL
echo.

REM PHASE 3: Create database
echo [PHASE 3] Setting up database...

mysql -u root -p%MYSQL_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS quantfolio;" >nul 2>&1
echo OK Database ready

mysql -u root -p%MYSQL_PASSWORD% quantfolio < db\schema.sql >nul 2>&1
echo OK Schema loaded

mysql -u root -p%MYSQL_PASSWORD% quantfolio < db\seed_data.sql >nul 2>&1
echo OK Seed data loaded

mysql -u root -p%MYSQL_PASSWORD% quantfolio < db\views.sql >nul 2>&1
echo OK Views created

mysql -u root -p%MYSQL_PASSWORD% quantfolio < db\triggers.sql >nul 2>&1
echo OK Triggers created
echo.

REM PHASE 4: Update db.properties with entered password
echo [PHASE 4] Updating credentials...
echo db.url=jdbc:mysql://localhost:3306/quantfolio?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC > src\main\resources\db.properties
echo db.user=root >> src\main\resources\db.properties
echo db.password=%MYSQL_PASSWORD% >> src\main\resources\db.properties
echo OK db.properties updated
echo.

REM PHASE 5: Build with Maven
echo [PHASE 5] Building with Maven...
echo.
call mvn clean package -q
if %errorlevel% neq 0 (
    echo.
    echo X Build failed
    pause
    exit /b 1
)
echo OK Build successful
echo.

REM PHASE 6: Launch Swing app
echo [PHASE 6] Launching Swing desktop application...
echo.
java -cp "target\classes;target\quantfolio\WEB-INF\lib\*" com.quantfolio.desktop.DesktopApp

pause
