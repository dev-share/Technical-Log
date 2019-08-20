FROM mysql:5.7.27

RUN echo "max_connections=1000" >> /etc/mysql/conf.d/docker.cnf
RUN echo "log-bin=mysql-bin" >> /etc/mysql/conf.d/docker.cnf
RUN echo "binlog-format=ROW" >> /etc/mysql/conf.d/docker.cnf
RUN echo "server-id=1" >> /etc/mysql/conf.d/docker.cnf
RUN echo "max_binlog_size=100M" >> /etc/mysql/conf.d/docker.cnf
RUN echo "expire_logs_days=7" >> /etc/mysql/conf.d/docker.cnf
RUN echo "event_scheduler=1" >> /etc/mysql/conf.d/docker.cnf
RUN echo "sync_binlog=500" >> /etc/mysql/conf.d/docker.cnf
RUN echo "innodb_flush_log_at_trx_commit=2" >> /etc/mysql/conf.d/docker.cnf
