@echo off
echo ========================================================
echo  Running GlobalDocs Solutions CLI Demonstration
echo ========================================================

if not exist bin\com\globaldocs\GlobalDocsApplication.class (
    echo Building project classes...
    call compile.bat
)

java -cp bin com.globaldocs.GlobalDocsApplication --cli
pause
