FROM mysql:5.7.28

RUN echo "max_connections=1000" >> /etc/mysql/conf.d/docker.cnf
RUN echo "log-bin=mysql-bin" >> /etc/mysql/conf.d/docker.cnf
RUN echo "binlog-format=ROW" >> /etc/mysql/conf.d/docker.cnf
RUN echo "server-id=1" >> /etc/mysql/conf.d/docker.cnf
RUN echo "max_binlog_size=100M" >> /etc/mysql/conf.d/docker.cnf
RUN echo "expire_logs_days=3" >> /etc/mysql/conf.d/docker.cnf
RUN echo "connect_timeout=30" >> /etc/mysql/conf.d/docker.cnf
RUN echo "default-time-zone='+8:00'" >> /etc/mysql/conf.d/docker.cnf
RUN echo "event_scheduler=1" >> /etc/mysql/conf.d/docker.cnf
RUN echo "sync_binlog=500" >> /etc/mysql/conf.d/docker.cnf
RUN echo "innodb_flush_log_at_trx_commit=2" >> /etc/mysql/conf.d/docker.cnf
RUN echo "explicit_defaults_for_timestamp=false" >> /etc/mysql/conf.d/docker.cnf

# Add a healthcheck (default every 30 secs)
HEALTHCHECK  --interval=60s --timeout=10s --retries=5 CMD /usr/bin/mysqladmin -uroot -p$(echo $MYSQL_ROOT_PASSWORD) ping | grep alive || exit 1

