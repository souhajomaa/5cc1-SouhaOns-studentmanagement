# Image de l'application student-management (Spring Boot, Java 17)
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="equipe-devops@esprit.tn"
WORKDIR /app
# Le jar est produit par "mvn package" dans le dossier target/
COPY target/student-management-0.0.1-SNAPSHOT.jar app.jar
# Port défini dans application.properties (server.port=8089)
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
