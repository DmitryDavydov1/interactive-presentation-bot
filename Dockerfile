# Этап сборки
FROM eclipse-temurin:23-jdk-alpine as builder

WORKDIR /app

COPY mvnw ./
COPY .mvn/ .mvn/
COPY pom.xml .
COPY src ./src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Финальный минимальный образ
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "/app/app.jar"]
