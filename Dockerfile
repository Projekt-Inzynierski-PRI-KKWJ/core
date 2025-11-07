FROM maven:3.6.3-openjdk-17-slim AS MAVEN_BUILD
COPY . /
RUN mvn -DskipTests=true package

FROM eclipse-temurin:21-jdk
EXPOSE 8080
COPY --from=MAVEN_BUILD pri-application/target/pri-application-*.jar /app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]
