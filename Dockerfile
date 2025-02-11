FROM openjdk:17-jdk-slim

LABEL authors="shubi"

WORKDIR /app

COPY target/TicketBookingSystem-1.0-SNAPSHOT.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.war"]