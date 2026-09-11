@echo off
echo ========================================================
echo  Starting GlobalDocs Solutions HTTP Web Server
echo ========================================================

if not exist bin\com\globaldocs\GlobalDocsApplication.class (
    echo Building project classes...
    call compile.bat
)

echo Launching Java Server on http://localhost:8080/
java -cp bin com.globaldocs.GlobalDocsApplication --port 8080
pause
