## 一、构建Dockerfile
```Dockerfile
FROM openjdk:8-jdk-alpine
RUN \
    mkdir -p /opt/deploy /opt/deploy/target-calculate-service  && \
    /bin/rm -rf /opt/deploy/target-calculate-service && \
    true

COPY target/target-calculate-service-1.0.0.jar /opt/deploy/target-calculate-service.jar
WORKDIR /opt/deploy
ENTRYPOINT ["java", "-jar", "target-calculate-service.jar" ]
```
## 二、Jenkins部署
```bash
if [ -n "$(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-calculate-service:1.0.0|awk '{print $1}')" ] ; then
	docker stop $(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-calculate-service:1.0.0|awk '{print $1}')
	docker rm -f $(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-calculate-service:1.0.0|awk '{print $1}')
fi
if [ -n "$(docker images -a|grep -v grep|grep 172.21.32.31:5000/target-calculate-service|awk '{print $3}')" ] ; then
	docker rmi -f $(docker images -a|grep -v grep|grep 172.21.32.31:5000/target-calculate-service|awk '{print $3}')
fi
docker build -t 172.21.32.31:5000/target-calculate-service:1.0.0 .
docker push 172.21.32.31:5000/target-calculate-service:1.0.0
docker run -itd -p 9731:9731 -p 65534:65534 --name target-calculate-service --privileged=true  -v /etc/localtime:/etc/localtime 172.21.32.31:5000/target-calculate-service:1.0.0

```