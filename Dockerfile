FROM maven:3-amazoncorretto-23-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package

FROM amazoncorretto:23
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
COPY src/docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
