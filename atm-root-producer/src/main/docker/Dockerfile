FROM registry.access.redhat.com/ubi8/openjdk-17:latest

COPY ../../../target/atm-root-producer-1.0-SNAPSHOT.jar /deployments/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
CMD ["java", "-jar", "/deployments/app.jar"]