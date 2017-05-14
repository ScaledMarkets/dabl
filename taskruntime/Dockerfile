# Container for running a DABL task.

FROM registry.hub.docker.com/alpine
# See https://hub.docker.com/_/alpine/
# Perhaps use registry.hub.docker.com/anapsix/alpine-java:8_server-jre
# See https://hub.docker.com/r/anapsix/alpine-java/
# See https://github.com/anapsix/docker-alpine-java

RUN mkdir /taskruntime
ADD ["taskruntime.jar", "/taskruntime/"]
WORKDIR /taskruntime/
ENTRYPOINT ["java", "-jar", "taskruntime.jar"]
