FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /code

COPY pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

COPY ["src/main", "/code/src/main"]
RUN ["mvn", "package"]

FROM eclipse-temurin:17-jdk-alpine AS package
COPY --from=build /code/target/authentication-app.jar /

CMD ["java", "-jar", "/authentication-app.jar"]