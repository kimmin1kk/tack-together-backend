FROM maven:3.8.2-amazoncorretto-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package
FROM amazoncorretto:11
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "/app/app.jar"]