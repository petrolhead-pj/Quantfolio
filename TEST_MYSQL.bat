@echo off
setlocal enabledelayedexpansion

echo Testing MySQL connection...
echo.

REM Test 1: Port 3307 without quotes
echo [Test 1] Trying port 3307 without quotes
mysql -u root -p"VedantMyRootSQL@242" -P 3307 -e "SELECT 1;" 2>&1
if %errorlevel% equ 0 (
    echo ✓ SUCCESS on port 3307
    exit /b 0
)

REM Test 2: Port 3306 (default)
echo [Test 2] Trying port 3306 (default)
mysql -u root -p"VedantMyRootSQL@242" -P 3306 -e "SELECT 1;" 2>&1
if %errorlevel% equ 0 (
    echo ✓ SUCCESS on port 3306
    exit /b 0
)

REM Test 3: No port specified
echo [Test 3] Trying default connection
mysql -u root -p"VedantMyRootSQL@242" -e "SELECT 1;" 2>&1
if %errorlevel% equ 0 (
    echo ✓ SUCCESS on default port
    exit /b 0
)

echo.
echo ✗ All connection attempts failed
echo.
echo Troubleshooting steps:
echo 1. Open services.msc and verify MySQL97 is running
echo 2. Verify the password is correct
echo 3. Check if MySQL is on port 3306 instead of 3307
echo.
pause
