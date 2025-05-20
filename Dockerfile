FROM maven:3.8.3-openjdk-17 as build
WORKDIR /app
COPY pom.xml /app/pom.xml
RUN mvn dependency:go-offline -B
COPY src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:25-ea-17-slim
COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
