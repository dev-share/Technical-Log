## 一、构建Dockerfile
```Dockerfile
FROM node:8.10.0
RUN \
    mkdir -p /opt/deploy /opt/deploy/target-manager-web  && \
    /bin/rm -rf /opt/deploy/target-manager-web && \
    true

COPY / /opt/deploy
WORKDIR /opt/deploy
#CMD ["npm","install"]
```
## 二、Jenkins部署
```bash

if [ -n "$(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-manager-web:1.0.0|awk '{print $1}')" ] ; then
	docker stop $(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-manager-web:1.0.0|awk '{print $1}')
	docker rm -f $(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-manager-web:1.0.0|awk '{print $1}')
fi
if [ -n "$(docker images -a|grep -v grep|grep 172.21.32.31:5000/target-manager-web|awk '{print $3}')" ] ; then
	docker rmi -f $(docker images -a|grep -v grep|grep 172.21.32.31:5000/target-manager-web|awk '{print $3}')
fi

docker build -t 172.21.32.31:5000/target-manager-web:1.0.0 .
docker push 172.21.32.31:5000/target-manager-web:1.0.0
docker run -itd -p 9091:9090 --name target-manager-web --privileged=true  -v /etc/localtime:/etc/localtime 172.21.32.31:5000/target-manager-web:1.0.0
docker exec -i -u root "$(docker ps -a|grep -v grep|grep 172.21.32.31:5000/target-manager-web:1.0.0|awk '{print $1}')" bash
ls
#docker cp ajax.js c8e7cdb4d64b:/opt/deploy/src/api
npm install
nohup npm run dev > /dev/null 2>&1 &
ls

```