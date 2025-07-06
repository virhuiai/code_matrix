@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------
@REM 中文注释：此部分声明了代码的版权和许可信息，表明代码遵循Apache 2.0许可协议，未经许可不得用于其他用途。

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------
@REM 中文注释：
@REM 1. 脚本功能：启动Maven构建工具的批处理脚本。
@REM 2. 必要环境变量：
@REM    - JAVA_HOME：JDK安装目录的路径，必须设置。
@REM 3. 可选环境变量：
@REM    - M2_HOME：Maven2安装目录的路径。
@REM    - MAVEN_BATCH_ECHO：设为'on'时，启用批处理命令的回显。
@REM    - MAVEN_BATCH_PAUSE：设为'on'时，脚本结束前等待用户按键。
@REM    - MAVEN_OPTS：传递给Java虚拟机的参数，例如用于调试Maven的设置。
@REM    - MAVEN_SKIP_RC：设为非空时，禁用加载mavenrc配置文件。
@REM 4. 重要配置参数说明：这些变量控制脚本的行为，如调试、暂停和命令回显，方便用户根据需求调整运行方式。

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@REM 中文注释：禁用命令回显，确保即使MAVEN_BATCH_ECHO为'on'，注释也不会显示在命令行中。

@REM set title of command window
title %0
@REM 中文注释：设置命令行窗口的标题为脚本文件名，便于识别。

@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%
@REM 中文注释：如果MAVEN_BATCH_ECHO设为'on'，则启用命令回显，显示执行的命令。

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")
@REM 中文注释：如果未设置HOME环境变量，则将其设置为用户的主目录（HOMEDRIVE和HOMEPATH的组合）。

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*
:skipRcPre
@REM 中文注释：
@REM 1. 功能：执行用户定义的预处理脚本（mavenrc_pre.bat或mavenrc_pre.cmd）。
@REM 2. 逻辑：如果MAVEN_SKIP_RC未设置，则检查用户目录下是否存在预处理脚本并执行。
@REM 3. 参数说明：%*表示将所有命令行参数传递给预处理脚本。
@REM 4. 注意事项：支持.bat和.cmd两种扩展名以兼容旧版和新版脚本。

@setlocal
@REM 中文注释：启用本地化环境变量，防止脚本中的变量修改影响全局环境。

set ERROR_CODE=0
@REM 中文注释：初始化错误代码为0，表示脚本初始状态为成功。

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal
@REM 中文注释：再次启用本地化环境变量，进一步隔离内部变量，避免后续脚本干扰。

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome
@REM 中文注释：开始验证环境变量，检查是否设置了JAVA_HOME。

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
@REM 中文注释：
@REM 1. 功能：如果JAVA_HOME未设置，输出错误信息到标准错误流。
@REM 2. 提示用户设置JAVA_HOME为Java安装目录的路径。
@REM 3. 注意事项：错误信息通过>&2输出到标准错误流，确保与标准输出分离。

goto error
@REM 中文注释：跳转到错误处理标签，终止脚本执行。

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init
@REM 中文注释：检查JAVA_HOME目录下是否存在java.exe可执行文件。

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
@REM 中文注释：
@REM 1. 功能：如果JAVA_HOME目录无效（不存在java.exe），输出错误信息。
@REM 2. 显示当前的JAVA_HOME值，并提示用户设置为正确的Java安装路径。
@REM 3. 注意事项：错误信息输出到标准错误流，确保清晰区分。

goto error
@REM 中文注释：跳转到错误处理标签，终止脚本执行。

@REM ==== END VALIDATION ====

:init
@REM 中文注释：初始化阶段，验证通过后开始设置Maven执行环境。

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir
@REM 中文注释：
@REM 1. 功能：查找项目根目录（包含.mvn文件夹的目录）。
@REM 2. 逻辑：如果MAVEN_BASEDIR已设置，则直接使用；否则继续查找。

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
@REM 中文注释：
@REM 1. 关键变量：
@REM    - EXEC_DIR：保存当前工作目录。
@REM    - WDIR：用于递归查找项目根目录的工作目录变量。

:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir
@REM 中文注释：
@REM 1. 功能：递归查找包含.mvn文件夹的目录。
@REM 2. 逻辑：检查当前目录是否存在.mvn文件夹，若不存在则切换到上级目录继续查找，直到无法再向上或找到.mvn。
@REM 3. 注意事项：如果当前目录等于上一级目录（即到达根目录），则停止查找。

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
@REM 中文注释：
@REM 1. 功能：找到项目根目录后，设置MAVEN_PROJECTBASEDIR为找到的目录。
@REM 2. 逻辑：恢复到原始执行目录（EXEC_DIR），确保后续操作在正确目录下进行。

goto endDetectBaseDir
@REM 中文注释：跳转到结束查找根目录的标签。

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"
@REM 中文注释：
@REM 1. 功能：如果未找到.mvn文件夹，则将当前工作目录作为项目根目录。
@REM 2. 逻辑：恢复到原始执行目录。

:endDetectBaseDir
@REM 中文注释：结束项目根目录的查找过程。

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig
@REM 中文注释：检查是否存在jvm.config文件，用于读取额外的JVM配置。

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
@endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%
@REM 中文注释：
@REM 1. 功能：读取.mvn/jvm.config文件中的JVM配置参数。
@REM 2. 逻辑：逐行读取文件内容并追加到JVM_CONFIG_MAVEN_PROPS变量。
@REM 3. 注意事项：使用EnableDelayedExpansion确保变量动态扩展，正确处理多行配置。

:endReadAdditionalConfig
@REM 中文注释：结束读取额外配置的过程。

SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
@REM 中文注释：
@REM 1. 关键变量：
@REM    - MAVEN_JAVA_EXE：Java可执行文件的路径。
@REM    - WRAPPER_JAR：Maven Wrapper的JAR文件路径。
@REM    - WRAPPER_LAUNCHER：Maven Wrapper的主类全限定名。

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
@REM 中文注释：
@REM 1. 功能：定义Maven Wrapper JAR文件的默认下载URL。
@REM 2. 默认值：指向Maven中央仓库的3.1.0版本JAR文件。

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)
@REM 中文注释：
@REM 1. 功能：读取maven-wrapper.properties文件，获取自定义的wrapperUrl。
@REM 2. 逻辑：如果文件中定义了wrapperUrl，则覆盖默认的DOWNLOAD_URL。

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
@REM This allows using the maven wrapper in projects that prohibit checking in binary data.
if exist %WRAPPER_JAR% (
    if "%MVNW_VERBOSE%" == "true" (
        echo Found %WRAPPER_JAR%
    )
) else (
    if not "%MVNW_REPOURL%" == "" (
        SET DOWNLOAD_URL="%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    )
    if "%MVNW_VERBOSE%" == "true" (
        echo Couldn't find %WRAPPER_JAR%, downloading it ...
        echo Downloading from: %DOWNLOAD_URL%
    )

    powershell -Command "&{"^
		"$webclient = new-object System.Net.WebClient;"^
		"if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
		"$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
		"}"^
		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"^
		"}"
    if "%MVNW_VERBOSE%" == "true" (
        echo Finished downloading %WRAPPER_JAR%
    )
)
@REM 中文注释：
@REM 1. 功能：检查并自动下载Maven Wrapper JAR文件。
@REM 2. 逻辑：
@REM    - 如果WRAPPER_JAR存在，输出找到信息（如果MVNW_VERBOSE为true）。
@REM    - 如果不存在，检查MVNW_REPOURL是否设置，若设置则更新DOWNLOAD_URL。
@REM    - 使用PowerShell下载JAR文件，支持认证（MVNW_USERNAME和MVNW_PASSWORD）。
@REM 3. 注意事项：
@REM    - 使用TLS 1.2协议确保下载安全。
@REM    - 仅在MVNW_VERBOSE为true时输出下载进度信息。
@REM 4. 特殊处理：允许在禁止提交二进制文件的项目中动态下载Wrapper JAR。

@REM End of extension

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
set MAVEN_CMD_LINE_ARGS=%*
@REM 中文注释：
@REM 1. 功能：保存命令行参数到MAVEN_CMD_LINE_ARGS变量。
@REM 2. 用途：确保参数在Windows和非Windows环境下都能正确传递。

%MAVEN_JAVA_EXE% ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
@REM 中文注释：
@REM 1. 功能：执行Maven Wrapper主程序。
@REM 2. 参数说明：
@REM    - MAVEN_JAVA_EXE：Java可执行文件。
@REM    - JVM_CONFIG_MAVEN_PROPS：从jvm.config读取的JVM参数。
@REM    - MAVEN_OPTS：用户定义的JVM参数。
@REM    - MAVEN_DEBUG_OPTS：用于调试的JVM参数。
@REM    - -classpath %WRAPPER_JAR%：指定Maven Wrapper JAR文件的类路径。
@REM    - -Dmaven.multiModuleProjectDirectory：设置多模块项目的根目录。
@REM    - WRAPPER_LAUNCHER：Maven Wrapper主类。
@REM    - MAVEN_CONFIG：Maven配置文件参数。
@REM    - %*：传递所有命令行参数。
@REM 3. 逻辑：通过Java运行Maven Wrapper，执行Maven构建任务。

if ERRORLEVEL 1 goto error
@REM 中文注释：检查Java执行的返回码，若大于0则跳转到错误处理。

goto end
@REM 中文注释：跳转到脚本结束标签。

:error
set ERROR_CODE=1
@REM 中文注释：设置错误代码为1，表示脚本执行失败。

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
@REM 中文注释：
@REM 1. 功能：结束本地化环境并保留ERROR_CODE的值。
@REM 2. 逻辑：将错误代码传递到外部环境。

if not "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@REM check for post script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_post.bat" call "%USERPROFILE%\mavenrc_post.bat"
if exist "%USERPROFILE%\mavenrc_post.cmd" call "%USERPROFILE%\mavenrc_post.cmd"
:skipRcPost
@REM 中文注释：
@REM 1. 功能：执行用户定义的后处理脚本（mavenrc_post.bat或mavenrc_post.cmd）。
@REM 2. 逻辑：如果MAVEN_SKIP_RC未设置，则检查并执行后处理脚本。
@REM 3. 注意事项：支持.bat和.cmd两种扩展名。

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if "%MAVEN_BATCH_PAUSE%"=="on" pause
@REM 中文注释：
@REM 1. 功能：如果MAVEN_BATCH_PAUSE为'on'，暂停脚本等待用户按键。
@REM 2. 用途：便于用户查看脚本输出或调试。

if "%MAVEN_TERMINATE_CMD%"=="on" exit %ERROR_CODE%
@REM 中文注释：
@REM 1. 功能：如果MAVEN_TERMINATE_CMD为'on'，终止脚本并返回错误代码。
@REM 2. 逻辑：控制脚本是否立即退出。

cmd /C exit /B %ERROR_CODE%
@REM 中文注释：
@REM 1. 功能：以指定的错误代码退出脚本。
@REM 2. 逻辑：通过cmd /C确保正确退出并返回ERROR_CODE。