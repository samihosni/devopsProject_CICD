# Use the official OpenJDK base image
FROM openjdk:17-jdk-slim

# Add the Spring Boot JAR file (ensure you build your JAR before)
COPY target/userManagementRH-0.0.1-SNAPSHOT.jar /app/userManagementRH-0.0.1-SNAPSHOT.jar

# Set the working directory to /app
WORKDIR /app

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "userManagementRH-0.0.1-SNAPSHOT.jar"]
