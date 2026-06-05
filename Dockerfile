FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw -B clean package

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache wget && addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/products-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/auth/login || exit 1
USER app
ENTRYPOINT ["java", "-jar", "app.jar"]
