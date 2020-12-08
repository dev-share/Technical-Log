#!/bin/bash
kid=$(ps -ef|grep Kafka|grep -v grep|awk '{print $2}')
if [ -n "$kid" ] ; then
	echo [`date`]Kafka process [$kid] is Running!
	kill -9 $kid;
	sleep 3;
fi
cd /opt/deploy/data/kafka
rm -rf logs/*
cd bin
sh kafka-server-stop.sh
sleep 3
sh kafka-server-start.sh ../config/server.properties > /dev/null 2>&1 &

pid=$(ps -ef|grep -v grep|grep Kafka|awk '{print $2}')
sleep 3 
echo "--Kafka pid:$pid"
cat ../logs/server.log

echo "cat logs/server.log"
