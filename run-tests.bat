@echo off
echo ========================================================
echo  Running GlobalDocs Automated Test Suite
echo ========================================================

if not exist bin\com\globaldocs\test\GlobalDocsTestRunner.class (
    echo Building project classes...
    call compile.bat
)

java -cp bin com.globaldocs.test.GlobalDocsTestRunner
pause
