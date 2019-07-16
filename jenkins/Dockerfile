FROM openjdk:8-jdk-alpine
RUN \
    mkdir -p /opt/deploy  && \
    /bin/rm -rf /opt/deploy/* && \
    true

COPY . /opt/deploy

RUN \
    chmod +x /opt/deploy/*  && \
    true

WORKDIR /opt/deploy
#ENTRYPOINT ["java", "-jar", "jenkins.war"]
CMD ["sh", "jenkins.sh"]