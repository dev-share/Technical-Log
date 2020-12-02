kill -9 $(ps -ef|grep -v grep|grep mysql|awk '{print $2}')
kill -9 $(ps -ef|grep -v grep|grep ndbd|awk '{print $2}')
rm -rf /tmp/*mysql*

cd /usr/local/mysql/mysql-cluster
ndb_mgmd -f /usr/local/mysql/mysql-cluster/cluster.cnf
sleep 3
cd /usr/local/mysql/mysqld
ips="`cat my.cnf |grep ndb-connectstring= |cut -d "=" -f 2`"
echo "--MySQL Cluster:$ips"
#ndbd --ndb-connectstring=172.21.32.183,172.21.32.184,172.21.32.185
ndbd --ndb-connectstring=$ips
sleep 3
kill -9 $(ps -ef|grep -v grep|grep mysqld|awk '{print $2}')
cd /usr/local/mysql/mysqld
echo "" > error.log
./mysql restart
ndb_mgm -e SHOW
