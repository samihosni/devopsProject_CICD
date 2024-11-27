# Use the official OpenJDK base image
FROM openjdk:17-jdk-slim

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Add the Spring Boot JAR file (ensure you build your JAR before)
ADD .target/userManagementRH-0.0.1-SNAPSHOT.jar userManagementRH-0.0.1-SNAPSHOT.jar


# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "devopsprojectcicd.jar"]
