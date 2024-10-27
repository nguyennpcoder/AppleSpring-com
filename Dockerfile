# Use Maven with Java 20
FROM maven:3.9.4-eclipse-temurin-20 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use Amazon Corretto 20
FROM amazoncorretto:20-alpine3.17
COPY --from=build /target/apple-0.0.1-SNAPSHOT.jar apple.jar
EXPOSE 8899
ENTRYPOINT ["java","-jar","apple.jar"]