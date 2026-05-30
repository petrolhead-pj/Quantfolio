@echo off
cd /d "C:\Users\paarth jain\Documents\Quant\Quantfolio"

echo.
echo Pushing Quantfolio to GitHub...
echo.

git add .
git status

echo.
set /p COMMIT_MSG="Enter commit message (or press Enter for default): "
if "%COMMIT_MSG%"=="" set COMMIT_MSG=Update Quantfolio project

git commit -m "%COMMIT_MSG%"
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo SUCCESS - Code pushed to https://github.com/petrolhead-pj/Quantfolio
) else (
    echo.
    echo Push failed. Make sure you are logged in to GitHub.
    echo Run: git config --global credential.helper manager
    echo Then try pushing again.
)

echo.
pause
