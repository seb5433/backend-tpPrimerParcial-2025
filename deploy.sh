#!/bin/bash

# Script para automatizar la construcción y despliegue de la aplicación en WildFly

echo "Comenzando proceso de deploy..."

# Navegar al directorio del backend
cd "$(dirname "$0")/backend" || { echo "No se pudo ingresar al directorio /backend"; exit 1; }
echo "Directorio actual: $(pwd)"

# Run Maven build
echo "Construyendo el proyecto Maven..."
mvn clean install

# Verificar que la construcción haya sido exitosa
if [ $? -ne 0 ]; then
    echo "Error al construir el proyecto Maven!"
    exit 1
fi

echo "Construcción exitosa!"

# Path al archivo WAR
WAR_FILE="target/parcial.war"

# Verificar que el archivo WAR exista
if [ ! -f "$WAR_FILE" ]; then
    echo "No se encontró el archivo WAR en $WAR_FILE"
    exit 1
fi

# Path al directorio de despliegue de WildFly
WILDFLY_DEPLOYMENTS="../wildfly-18.0.1.Final/standalone/deployments/"

echo "Copiando $WAR_FILE a $WILDFLY_DEPLOYMENTS"
cp "$WAR_FILE" "$WILDFLY_DEPLOYMENTS" || { echo "Fallo el copiado del archivo!"; exit 1; }

echo "Despliegue exitoso!"


# Reiniciar WildFly
cd ../wildfly-18.0.1.Final/bin || { echo "No se pudo ingresar al directorio /wildfly-18.0.1.Final/bin"; exit 1; }
echo "Directorio actual: $(pwd)"

# Cerrar Wildfly
./jboss-cli.sh --connect command=:shutdown

# Iniciar WildFly
./standalone.sh