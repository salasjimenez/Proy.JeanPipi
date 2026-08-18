# Imagen de produccion de JeanPipi.
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM jetty:10.0.26-jre17
USER root
RUN mkdir -p /var/lib/jeanpipi/uploads && chown -R jetty:jetty /var/lib/jeanpipi
USER jetty
COPY --from=build /app/target/JeanPipi.war /var/lib/jetty/webapps/ROOT.war
ENV JEANPIPI_UPLOAD_DIR=/var/lib/jeanpipi/uploads
EXPOSE 8080
