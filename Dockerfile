# Use an official OpenJDK runtime as a parent image (Java 17)
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/NotificationService-0.0.1-SNAPSHOT.jar notificationservice.jar

# Expose the port the app runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "notificationservice.jar"]