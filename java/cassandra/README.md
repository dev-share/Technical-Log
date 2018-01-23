1.下载Cassandra解压

2.配置环境变量

CASSANDRA_HOME=D:\WebServer\cassandra-3.11.0

Path:...;%CASSANDRA_HOME%\bin;

3.cmd命令启动Cassandra

powershell Set-ExecutionPolicy Unrestricted

cassandra.bat

4.创建keyspace

1)执行cqlsh.bat

2)cqlsh> create keyspace css_keyspace with replication = {'class':'SimpleStrategy', 'replication_factor':1};

5.Cassandra默认用户名密码：cassandra/cassandra
