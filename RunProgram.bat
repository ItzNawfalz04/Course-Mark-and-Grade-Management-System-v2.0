@echo off
cls

REM Go to the folder where this .bat file is located (root folder)
cd /d "%~dp0"

REM Create bin folder if it doesn't exist
if not exist "bin" mkdir "bin"

REM Compile all Java files from src into bin
javac -d "bin" src\*.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b
)

REM Run Main class from bin
java -cp "bin" Main
pause
