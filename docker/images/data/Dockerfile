FROM openjdk:8-jre

RUN \
    mkdir -p /opt/deploy

COPY *.tar.gz /opt/deploy

ARG APP_VERSION=1.0.0
ARG APP_DESCRIPTION="Spring Boot Project"

ENV APP_VERSION=${APP_VERSION}
LABEL version=${APP_VERSION}
LABEL description=${APP_DESCRIPTION}

RUN \
    tar -xzvf /opt/deploy/*.tar.gz -C /opt/deploy && \
    /bin/rm -rf /opt/deploy/*.tar.gz && \
    mkdir -p /opt/deploy/logs  /opt/deploy/data && \
    cp -vr /opt/deploy/*/* /opt/deploy && \
    mkdir -p /opt/deploy/bin && \
    chmod +x /opt/deploy/bin  && \
    mv /opt/deploy/*.jar /opt/deploy/sf-app.jar && \
    true

WORKDIR /opt/deploy
# Add a healthcheck (default every 30 secs)
#HEALTHCHECK CMD curl http://localhost:8080/docket/health || exit 1

ENTRYPOINT ["java", "-jar", "sf-app.jar" ]

