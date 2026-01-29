# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (this layer will be cached if pom.xml doesn't change)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["/bin/sh", "-c", "echo '🚀 [SHELL] Container started. Launching Java...'; exec java -Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
EXPOSE 8080
