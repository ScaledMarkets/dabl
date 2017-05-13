# Container for running a DABL task.
FROM docker.io/alpine
RUN mkdir /taskruntime
ADD ["taskruntime.jar", "/taskruntime/"]
WORKDIR /taskruntime/
CMD ["java", "-jar", "taskruntime.jar"]
