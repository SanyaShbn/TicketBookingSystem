FROM tomcat:11-jdk21-temurin-noble

LABEL authors="shubi"

COPY target/TicketBookingSystem-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

COPY tomcat/conf/context.xml /usr/local/tomcat/conf/

EXPOSE 8080

CMD ["catalina.sh", "run"]