# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17 as build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Package the Spring Boot application
RUN mvn clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy only the final JAR from the build stage
COPY --from=build /app/target/externalapi-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
