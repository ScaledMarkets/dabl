# Container for running a DABL task.

#FROM alpine

FROM anapsix/alpine-java

#FROM registry.hub.docker.com/alpine
# See https://hub.docker.com/_/alpine/
# Perhaps use registry.hub.docker.com/anapsix/alpine-java:8_server-jre
# See https://hub.docker.com/r/anapsix/alpine-java/
# See https://github.com/anapsix/docker-alpine-java

ADD ["parser.jar", "parser.jar"]
ADD ["common.jar", "common.jar"]
ADD ["task_runtime.jar", "task_runtime.jar"]
ADD ["junixsocket.jar", "junixsocket.jar"]

ENTRYPOINT ["java", "-jar", "task_runtime.jar"]
