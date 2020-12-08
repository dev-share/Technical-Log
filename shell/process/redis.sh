#!/bin/bash
cd /opt/deploy/data/redis
kid=$(ps -ef|grep redis-server|grep -v grep|awk '{print $2}')
if [ -n "$kid" ] ; then
	echo [`date`]redis process [$kid] is Running!
	kill -9 $kid;
	sleep 3;
fi
rm -rf */logs/* && rm -rf */*.pid
redis-server /opt/deploy/data/redis/6379/conf/redis.conf
redis-server /opt/deploy/data/redis/6389/conf/redis.conf
