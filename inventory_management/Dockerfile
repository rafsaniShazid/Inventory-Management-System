FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:17-jre-jammy
RUN groupadd --system spring && useradd --system --gid spring spring

WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8081
ENV JAVA_OPTS=""

USER spring:spring
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
