@echo off
REM GitHub Authentication & Push Script

cd /c/Users/pabar/OneDrive/Desktop/Quant/Quantfolio-main

echo.
echo ╔════════════════════════════════════════════════════════╗
echo ║  GitHub Authentication Required                        ║
echo ║                                                        ║
echo ║  This will authenticate your GitHub account via gh CLI ║
echo ║  and then push the Java 21 upgrade to your repo.      ║
echo ╚════════════════════════════════════════════════════════╝
echo.

REM Authenticate with GitHub
echo Authenticating with GitHub...
echo.
gh auth login --web

echo.
echo ✓ Authentication complete!
echo.

REM Push to GitHub
echo Pushing to GitHub repository...
git push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo ╔════════════════════════════════════════════════════════╗
    echo ║  ✓ SUCCESS!                                            ║
    echo ║                                                        ║
    echo ║  Changes pushed to:                                    ║
    echo ║  https://github.com/petrolhead-pj/Quantfolio         ║
    echo ║                                                        ║
    echo ║  What was pushed:                                      ║
    echo ║  • Java runtime upgraded to version 21 LTS           ║
    echo ║  • pom.xml updated (compiler 11 → 21)               ║
    echo ║  • All source code files                             ║
    echo ║  • Database scripts (schema & triggers)              ║
    echo ║  • QUICKSTART.md documentation                       ║
    echo ║                                                        ║
    echo ║  Next Step:                                           ║
    echo ║  Open VSCode and clone the repo to continue work    ║
    echo ╚════════════════════════════════════════════════════════╝
) else (
    echo ✗ Push failed. Check the error above.
)

echo.
pause
