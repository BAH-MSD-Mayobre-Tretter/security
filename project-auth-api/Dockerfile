FROM gradle:jdk10 as builder

# Alternative from page 78 of lecture book
# FROM openjkd:8

# Alternative from kaitlyn kelly github
# FROM gradle:6.6.1-jdk8 as builder



#  Creates a directory for storing custom service files
RUN mkdir -p /opt/my/service
COPY --chown=gradle:gradle . /opt/my/service
WORKDIR /opt/my/service


RUN gradle bootJar
FROM openjdk:8-jdk-alpine
EXPOSE 8091
ARG targethost=localhost:8090
ENV API_host=$targethost
VOLUME /tmp
ARG LIBS=app/build/libs
COPY --from=builder ${LIBS}/ /app/libs
ENTRYPOINT ["java","-jar","./app/libs/security-auth-0.0.1-SNAPSHOT.jar"]
