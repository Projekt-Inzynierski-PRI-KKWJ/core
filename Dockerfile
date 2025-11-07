FROM maven:3.6.3-openjdk-17-slim AS maven_build
COPY . /
RUN mvn -DskipTests=true package

FROM eclipse-temurin:21-jdk
EXPOSE 8080
COPY --from=maven_build pri-application/target/pri-application-*.jar /app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]
