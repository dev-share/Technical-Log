#!/bin/bash
kid=$(ps -ef|grep QuorumPeerMain|grep -v grep|awk '{print $2}')
if [ -n "$kid" ] ; then
	echo [`date`]Zookeeper process [$kid] is Running!
	kill -9 $kid;
	sleep 3;
fi
cd /opt/deploy/data/zookeeper
rm -rf logs/*
bin/zkServer.sh restart
sleep 5
bin/zkServer.sh status
