@echo off
cls

REM 
cd /d "%~dp0"

REM 
if not exist "bin" mkdir "bin"

REM 
javac -d "bin" src\Main\*.java src\Student\*.java src\Lecturer\*.java src\Admin\*.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b
)

REM 
java -cp "bin" Main.Main
pause