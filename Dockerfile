FROM 3-amazoncorretto-21 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM amazoncorretto:21
WORKDIR /app
ARG JAR_FILE=/app/target/*.jar
COPY --from=build $JAR_FILE app.jar
CMD ["java", "-jar", "app.jar"]