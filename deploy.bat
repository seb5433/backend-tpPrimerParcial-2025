@echo off
:: Script para automatizar la construcción y despliegue de la aplicación en WildFly

echo Comenzando proceso de deploy...

:: Navegar al directorio del backend
cd /D "%~dp0backend" || (
    echo No se pudo ingresar al directorio \backend
    exit /b 1
)
echo Directorio actual: %cd%

:: Run Maven build
echo Construyendo el proyecto Maven...
call mvn clean install

:: Verificar que la construcción haya sido exitosa
if errorlevel 1 (
    echo Error al construir el proyecto Maven!
    exit /b 1
)

echo Construcción exitosa!

:: Path al archivo WAR
set WAR_FILE=target\parcial.war

:: Verificar que el archivo WAR exista
if not exist "%WAR_FILE%" (
    echo No se encontró el archivo WAR en %WAR_FILE%
    exit /b 1
)

:: Path al directorio de despliegue de WildFly
set WILDFLY_DEPLOYMENTS=..\wildfly-18.0.1.Final\standalone\deployments\

echo Copiando %WAR_FILE% a %WILDFLY_DEPLOYMENTS%
copy "%WAR_FILE%" "%WILDFLY_DEPLOYMENTS%" || (
    echo Fallo el copiado del archivo!
    exit /b 1
)

echo Despliegue exitoso!

:: Reiniciar WildFly
cd ..\wildfly-18.0.1.Final\bin || (
    echo No se pudo ingresar al directorio \wildfly-18.0.1.Final\bin
    exit /b 1
)
echo Directorio actual: %cd%

echo Iniciando WildFly...
:: Iniciar WildFly
call standalone.bat

echo Cerrando Wildfly y reiniciando...
:: Cerrar Wildfly
call jboss-cli.bat --connect command=:shutdown

echo Iniciando WildFly...
:: Iniciar WildFly
call standalone.bat

