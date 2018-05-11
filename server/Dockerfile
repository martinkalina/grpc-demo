# ###################################################################################################
# Dockerfile for service/application image based on jar based service base image enriched by project's app artifact
# ###################################################################################################

FROM openjdk:my

EXPOSE 9898 8080

WORKDIR /app

COPY ./target/*.jar ./app.jar
#RUN ["sudo", "apt-get", "update"]
#RUN ["sudo", "apt-get", "install", "socat"]
CMD ["java", "-jar", "./app.jar"]
