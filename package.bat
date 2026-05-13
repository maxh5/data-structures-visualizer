@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM ============================================================
REM Build a self-contained Windows app image using jpackage.
REM
REM Produces:
REM   dist\DataStructuresVisualizer\DataStructuresVisualizer.exe
REM   dist\DataStructuresVisualizer-Windows.zip
REM
REM The .zip is what to send to your professor: she unzips it,
REM double-clicks the .exe, and the app runs. No Java needed on
REM her machine.
REM ============================================================

set "APP_NAME=DataStructuresVisualizer"
set "MAIN_CLASS=dsv.App"
set "JAVAFX_VERSION=25.0.2"
set "JAVAFX_LIB=C:\Program Files\Java\javafx-sdk-%JAVAFX_VERSION%\lib"
set "JAVAFX_JMODS_DIR=tools\javafx-jmods-%JAVAFX_VERSION%"
set "JAVAFX_JMODS_URL=https://download2.gluonhq.com/openjfx/%JAVAFX_VERSION%/openjfx-%JAVAFX_VERSION%_windows-x64_bin-jmods.zip"

pushd "%~dp0"

where javac    >nul 2>&1 || ( echo [ERROR] javac not on PATH    & goto :fail )
where jar      >nul 2>&1 || ( echo [ERROR] jar not on PATH      & goto :fail )
where jpackage >nul 2>&1 || ( echo [ERROR] jpackage not on PATH & goto :fail )

if not exist "%JAVAFX_LIB%" (
    echo [ERROR] JavaFX SDK not found at:
    echo         %JAVAFX_LIB%
    echo         Edit JAVAFX_LIB at the top of this script.
    goto :fail
)

echo [1/6] Cleaning build and dist directories...
if exist build rmdir /s /q build
if exist dist  rmdir /s /q dist
mkdir build\classes
mkdir build\jar
mkdir dist

echo [2/6] Ensuring JavaFX jmods are available...
if not exist "%JAVAFX_JMODS_DIR%\javafx.graphics.jmod" (
    if not exist tools mkdir tools
    echo        Downloading %JAVAFX_JMODS_URL%
    echo        ^(~45 MB, one-time^)...
    powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%JAVAFX_JMODS_URL%' -OutFile 'tools\javafx-jmods.zip' -UseBasicParsing"
    if errorlevel 1 ( echo [ERROR] Failed to download JavaFX jmods. & goto :fail )
    echo        Extracting...
    powershell -NoProfile -Command "Expand-Archive -Force 'tools\javafx-jmods.zip' 'tools'"
    if errorlevel 1 ( echo [ERROR] Failed to extract JavaFX jmods. & goto :fail )
    del /q tools\javafx-jmods.zip
    if not exist "%JAVAFX_JMODS_DIR%\javafx.graphics.jmod" (
        echo [ERROR] Expected jmods folder not found after extraction:
        echo         %JAVAFX_JMODS_DIR%
        goto :fail
    )
)

echo [3/6] Compiling Java sources...
dir /s /b src\*.java > build\sources.txt
javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -d build\classes @build\sources.txt
if errorlevel 1 ( echo [ERROR] Compilation failed. & goto :fail )

echo [4/6] Copying resources ^(FXML, images^)...
robocopy src build\classes /e /xf *.java /njh /njs /nfl /ndl >nul
if errorlevel 8 ( echo [ERROR] robocopy failed. & goto :fail )

echo [5/6] Building dsv.jar...
jar --create --file build\jar\dsv.jar --main-class %MAIN_CLASS% -C build\classes .
if errorlevel 1 ( echo [ERROR] jar packaging failed. & goto :fail )

echo [6/6] Running jpackage ^(this takes a minute^)...
jpackage ^
    --type app-image ^
    --name "%APP_NAME%" ^
    --input build\jar ^
    --main-jar dsv.jar ^
    --main-class %MAIN_CLASS% ^
    --module-path "%JAVAFX_JMODS_DIR%" ^
    --add-modules javafx.controls,javafx.fxml ^
    --java-options "--enable-native-access=javafx.graphics" ^
    --dest dist
if errorlevel 1 ( echo [ERROR] jpackage failed. & goto :fail )

echo Compressing dist\%APP_NAME% into ZIP...
powershell -NoProfile -Command "Compress-Archive -Force -Path 'dist\%APP_NAME%' -DestinationPath 'dist\%APP_NAME%-Windows.zip'"
if errorlevel 1 ( echo [WARN] Could not create zip. The folder is still in dist\. & goto :done )

echo.
echo [OK] Build complete.
echo      App folder: dist\%APP_NAME%\
echo      .exe:       dist\%APP_NAME%\%APP_NAME%.exe
echo      Zip:        dist\%APP_NAME%-Windows.zip
goto :done

:fail
popd
endlocal
exit /b 1

:done
popd
endlocal
exit /b 0
