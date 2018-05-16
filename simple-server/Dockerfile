# ###################################################################################################
# Dockerfile for service/application image based on jar based service base image enriched by project's app artifact
# ###################################################################################################

FROM openjdk:10.0.1-jdk-slim

EXPOSE 9898

WORKDIR /app

COPY ./target/grpc-simple-server*.jar ./app.jar
#RUN ["sudo", "apt-get", "update"]
#RUN ["sudo", "apt-get", "install", "socat"]
CMD ["java", "-jar", "./app.jar", "-Xmx8M"]