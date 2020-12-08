#!/bin/bash
kid=$(ps -ef|grep mysqld|grep -v grep|awk '{print $2}')
if [ -n "$kid" ] ; then
	echo [`date`]mysql process [$kid] is Running!
	kill -9 $kid;
	sleep 3;
fi

nid=$(ps -ef|grep ndb_mgmd|grep -v grep|awk '{print $2}')
if [ -n "$nid" ] ; then
	echo [`date`]ndb_mgmd process [$nid] is Running!
	kill -9 $nid;
	sleep 3;
fi

nbid=$(ps -ef|grep ndbd|grep -v grep|awk '{print $2}')
if [ -n "$nbid" ] ; then
	echo [`date`]ndbd process [$nbid] is Running!
	kill -9 $nbid;
	sleep 3;
fi

rm -rf /tmp/*mysql*

cd /usr/local/mysql/mysql-cluster
bin/ndb_mgmd -f /usr/local/mysql/mysql-cluster/cluster.cnf
sleep 3
cd /usr/local/mysql/mysqld
ips="`cat my.cnf |grep ndb-connectstring= |cut -d "=" -f 2`"
echo "[`date`]MySQL Cluster:$ips"
bin/ndbd --ndb-connectstring=$ips
sleep 3
cd /usr/local/mysql/mysqld
echo "" > error.log
./mysql restart
bin/ndb_mgm -e SHOW
