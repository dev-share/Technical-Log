#!/bin/bash
cd /opt/deploy/data/elasticsearch
kid=$(ps -ef|grep Elasticsearch|grep -v grep|awk '{print $2}')

if [ -n "$kid" ] ; then
	echo [`date`]Elasticsearch process [$kid] is Running!
	kill -9 $kid;
	sleep 3;
fi
rm -rf /tmp/*elasticsearch*
#rm -rf data/*
rm -rf logs/*
sudo -u elasticsearch -p elasticsearch bin/elasticsearch -d
