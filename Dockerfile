FROM maven:3.9.13-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /workspace
COPY . .
RUN mvn -B -f "$SERVICE/pom.xml" -DskipTests package
RUN cp "$SERVICE"/target/*.jar /app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
