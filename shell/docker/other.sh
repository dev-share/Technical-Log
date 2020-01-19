docker pull registry:2.7.1
docker pull jc21/registry-ui:2.0.2
docker pull joxit/docker-registry-ui:1.4
docker pull nginx:1.17.4-alpine
docker pull eclipse-mosquitto:1.4.12
docker pull eclipse-mosquitto:1.6.7
docker pull phpmyadmin/phpmyadmin:4.9
docker pull neo4j:3.4
docker pull neo4j:3.5.12
docker pull influxdb:1.5.4-alpine
docker pull influxdb:1.7.8-alpine
docker pull influxdb:1.7.8
docker pull canal/canal-server:v1.1.3
docker pull canal/canal-server:v1.1.4
docker pull canal/canal-admin:v1.1.4
docker pull mongo:4.2.1
docker pull mongo-express:0.49.0

docker tag registry:2.7.1 172.21.32.102:5000/registry:2.7.1
docker tag jc21/registry-ui:2.0.2 172.21.32.102:5000/jc21/registry-ui:2.0.2
docker tag joxit/docker-registry-ui:1.4 172.21.32.102:5000/joxit/docker-registry-ui:1.4
docker tag nginx:1.17.4-alpine 172.21.32.102:5000/nginx:1.17.4-alpine
docker tag eclipse-mosquitto:1.4.12 172.21.32.102:5000/eclipse-mosquitto:1.4.12
docker tag eclipse-mosquitto:1.6.7 172.21.32.102:5000/eclipse-mosquitto:1.6.7
docker tag phpmyadmin/phpmyadmin:4.9 172.21.32.102:5000/phpmyadmin/phpmyadmin:4.9
docker tag neo4j:3.4 172.21.32.102:5000/neo4j:3.4
docker tag neo4j:3.5.12 172.21.32.102:5000/neo4j:3.5.12
docker tag influxdb:1.5.4-alpine  172.21.32.102:5000/influxdb:1.5.4-alpine
docker tag influxdb:1.7.8-alpine  172.21.32.102:5000/influxdb:1.7.8-alpine
docker tag influxdb:1.7.8  172.21.32.102:5000/influxdb:1.7.8
docker tag canal/canal-server:v1.1.3 172.21.32.102:5000/canal/canal-server:v1.1.3
docker tag canal/canal-server:v1.1.4 172.21.32.102:5000/canal/canal-server:v1.1.4
docker tag canal/canal-admin:v1.1.4 172.21.32.102:5000/canal/canal-admin:v1.1.4
docker tag mongo:4.2.1 172.21.32.102:5000/mongo:4.2.1
docker tag mongo-express:0.49.0 172.21.32.102:5000/mongo-express:0.49.0


docker push 172.21.32.102:5000/registry:2.7.1
docker push 172.21.32.102:5000/jc21/registry-ui:2.0.2
docker push 172.21.32.102:5000/joxit/docker-registry-ui:1.4
docker push 172.21.32.102:5000/nginx:1.17.4-alpine
docker push 172.21.32.102:5000/eclipse-mosquitto:1.4.12
docker push 172.21.32.102:5000/eclipse-mosquitto:1.6.7
docker push 172.21.32.102:5000/phpmyadmin/phpmyadmin:4.9
docker push 172.21.32.102:5000/neo4j:3.4
docker push 172.21.32.102:5000/neo4j:3.5.12
docker push 172.21.32.102:5000/influxdb:1.5.4-alpine
docker push 172.21.32.102:5000/influxdb:1.7.8-alpine
docker push 172.21.32.102:5000/influxdb:1.7.8
docker push 172.21.32.102:5000/canal/canal-server:v1.1.3
docker push 172.21.32.102:5000/canal/canal-server:v1.1.4
docker push 172.21.32.102:5000/canal/canal-admin:v1.1.4
docker push 172.21.32.102:5000/mongo:4.2.1
docker push 172.21.32.102:5000/mongo-express:0.49.0

