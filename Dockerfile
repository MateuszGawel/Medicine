# Start from an official Java runtime image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the application JAR
COPY target/medicine-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
