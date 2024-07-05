# Usar una imagen de JDK 11
FROM eclipse-temurin:21-jre-alpine

# Configurar un directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR generado en el directorio de trabajo
COPY target/eureka-dan-0.0.1-SNAPSHOT.jar /app/eureka-server.jar

# Exponer el puerto 8761
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/eureka-server.jar"]
