FROM openjdk:8-jre
RUN \
    mkdir -p /opt/deploy
COPY target/target-manager-service-*.tar.gz /opt/deploy

ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION:-v1.0.0}

RUN \
    tar -xzvf /opt/deploy/target-manager-service-*.tar.gz -C /opt/deploy && \
    /bin/rm -rf /opt/deploy/target-manager-service-*.tar.gz && \
    mkdir -p /opt/deploy/target-manager-service/logs  /opt/deploy/target-manager-service/data && \
    chmod +x /opt/deploy/target-manager-service/bin/*.sh  && \
    mv /opt/deploy/target-manager-service/target-manager-service-*.jar /opt/deploy/target-manager-service/target-manager-service.jar && \
    true

WORKDIR /opt/deploy/target-manager-service
# Add a healthcheck (default every 30 secs)
HEALTHCHECK --interval=60s --timeout=10s --retries=5 CMD curl http://localhost:8631/swagger-ui.html || exit 1

ENTRYPOINT ["java", "-jar", "target-manager-service.jar" ]
