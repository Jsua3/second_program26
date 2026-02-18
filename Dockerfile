# Utiliza una imagen base de Eclipse Temurin (OpenJDK 17)
FROM eclipse-temurin:17-jdk-focal

# Establece el directorio de trabajo en /app
WORKDIR /app

# Copia el archivo JAR de la aplicación al contenedor
COPY target/app.jar app.jar

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
