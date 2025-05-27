# Build using Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk as build

# Set working directory to root of your Spring Boot project
WORKDIR /backend

# Copy everything into the image
COPY . .

# Build using Maven Wrapper, skipping tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk

# Working directory for runtime image
WORKDIR /backend

# Copy the built JAR from the build stage
COPY --from=build /backend/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]