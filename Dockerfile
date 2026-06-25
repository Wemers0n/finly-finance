# Estágio 1
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copia pom.xml para baixar dependências em cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src/main/java .
RUN mvn clean package -DskipTests

# Estágio 2
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

# Cria um usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]