@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo  Compiling GlobalDocs Solutions Java Application
echo ========================================================

if not exist bin mkdir bin

powershell -NoProfile -Command "Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { '\"' + $_.FullName.Replace('\', '/') + '\"' } | Out-File -Encoding ascii sources.txt"
javac -encoding UTF-8 -d bin @sources.txt
set COMPILE_STATUS=%ERRORLEVEL%
del sources.txt

if %COMPILE_STATUS% EQU 0 (
    echo [OK] Java compilation succeeded! All classes compiled to bin/
) else (
    echo [ERROR] Java compilation failed with exit code %COMPILE_STATUS%
    exit /b %COMPILE_STATUS%
)
