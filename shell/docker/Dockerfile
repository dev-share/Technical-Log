FROM openjdk:8-jre

RUN \
    mkdir -p /opt/deploy

COPY *.tar.gz /opt/deploy

ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION:-v1.0.0}

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
ENTRYPOINT ["java", "-jar", "sf-app.jar" ]

