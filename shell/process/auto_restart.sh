#!/bin/bash
cd /root
ifconfig ens224 down
/opt/deploy/data/redis/redis.sh  > /dev/null 2>&1 
/opt/deploy/data/zookeeper/zookeeper.sh && sleep 5 && /opt/deploy/data/kafka/kafka_ssl.sh  > /dev/null 2>&1
/opt/deploy/data/elasticsearch/elasticsearch.sh  > /dev/null 2>&1 
/usr/local/mysql/mysql.sh  > /dev/null 2>&1
ifconfig ens224 up
