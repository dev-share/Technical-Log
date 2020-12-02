cd /opt/deploy/data/redis
kill -9 $(ps -ef|grep -v grep|grep redis|awk '{print $2}')
rm -rf */logs/* && rm -rf */*.pid
redis-server /opt/deploy/data/redis/6379/conf/redis.conf
redis-server /opt/deploy/data/redis/6389/conf/redis.conf
