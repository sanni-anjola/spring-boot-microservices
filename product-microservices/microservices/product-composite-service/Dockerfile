FROM maven:3.6.3-openjdk-14-slim AS build
WORKDIR /build
# copy just pom.xml (dependencies and dowload them all for offline access later - cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B
# copy source files and compile them (.dockerignore should handle what to copy)
COPY . .
RUN mvn package

#191.13MB jdk-alpine
FROM openjdk:12-jdk-alpine

EXPOSE 8080

ADD ./target/product-composite-service.jar product-composite-service.jar

ENTRYPOINT ["java", "-jar", "/product-composite-service.jar"]