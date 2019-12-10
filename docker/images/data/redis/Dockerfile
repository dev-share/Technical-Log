FROM redis:5.0.7-alpine 
RUN mkdir -p /usr/local/etc/redis
RUN echo "tcp-keepalive 300" >> /usr/local/etc/redis/redis.conf
RUN echo "timeout 30" >> /usr/local/etc/redis/redis.conf
RUN echo "repl-ping-slave-period 60" >> /usr/local/etc/redis/redis.conf
RUN echo "hz 1" >> /usr/local/etc/redis/redis.conf
RUN echo "repl-timeout 60" >> /usr/local/etc/redis/redis.conf
RUN echo "rdbchecksum  no" >> /usr/local/etc/redis/redis.conf
RUN echo "dbfilename dump.rdb" >> /usr/local/etc/redis/redis.conf
RUN echo "save 900 1" >> /usr/local/etc/redis/redis.conf
RUN echo "save 300 10" >> /usr/local/etc/redis/redis.conf
RUN echo "save 60 10000" >> /usr/local/etc/redis/redis.conf
RUN echo "appendonly no" >> /usr/local/etc/redis/redis.conf
RUN echo "appendfilename dump.aof" >> /usr/local/etc/redis/redis.conf
RUN echo "appendfsync no" >> /usr/local/etc/redis/redis.conf
RUN echo "no-appendfsync-on-rewrite no" >> /usr/local/etc/redis/redis.conf
RUN echo "auto-aof-rewrite-percentage 100" >> /usr/local/etc/redis/redis.conf
RUN echo "auto-aof-rewrite-min-size 256mb" >> /usr/local/etc/redis/redis.conf
CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]
