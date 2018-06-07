# Canal安装与部署
## 1. MySQL的row模式以及赋权
- 开启MySQL的binlog功能并配置为row模式(my.ini)
```ini
  #log bin
  log-bin=mysql-bin
  #binlog mode: ROW mode
  binlog-format=ROW
  #master/slave模式log_slave_update这个配置一定要打开
  #log_slave_updates=true
  #config mysql replaction privileges，mustn't repeat with slaveId of canal
  server-id=1
```
-- canal用户赋权
```sql
CREATE USER canal IDENTIFIED BY 'canal';    
GRANT SELECT,SUPER, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';  
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;  
FLUSH PRIVILEGES;
```
-- 查看权限及模式
```sql
-- 查询canal权限
show grants for 'canal';
-- 查看binlog(log_bin必须为ON)
show variables like 'log_%';
-- canal用户查看状态（必须有解析点）
show master status;
```
## 2. 下载Canal并解压至指定路径
[Canal v1.0.25下载地址](https://github.com/alibaba/canal/releases/download/v1.0.25/canal.deployer-1.0.25.tar.gz)
[Canal最新版下载地址](https://github.com/alibaba/canal/releases/download/canal-1.0.26-preview-3/canal.deployer-1.0.26-SNAPSHOT.tar.gz)
## 3. 配置canal
- canal.properties配置（以下可修改，其他默认）
```properties
canal.id= 1
#配置IP地址（若为空将自动扫描地址）
canal.ip= 127.0.0.1
#canal端口
canal.port= 11111
#配置zookeeper注册中心(若为空则不使用注册中心)
canal.zkServers=
#canal当前server上部署的instance列表
canal.destinations= example,otter

canal.instance.tsdb.spring.xml=classpath:spring/tsdb/h2-tsdb.xml
#canal.instance.tsdb.spring.xml=classpath:spring/tsdb/mysql-tsdb.xml

#canal.instance.global.spring.xml = classpath:spring/local-instance.xml
#canal.instance.global.spring.xml = classpath:spring/memory-instance.xml
#canal.instance.global.spring.xml = classpath:spring/file-instance.xml
canal.instance.global.spring.xml = classpath:spring/default-instance.xml
```
- instance.properties配置（destinations配置后修改样例路径conf\example-->\conf\otter）
```properties
#实例slaveId(不与canal.id相同，集群也不相同，唯一的)
canal.instance.mysql.slaveId = 1234
#MySQL数据库配置
canal.instance.master.address = 127.0.0.1:3306
canal.instance.dbUsername = canal
canal.instance.dbPassword = canal
#默认数据库名称(可为空)
canal.instance.defaultDatabaseName = 
canal.instance.connectionCharset = UTF-8
# table regex
canal.instance.filter.regex=.*\\..*
# table black regex
canal.instance.filter.black.regex=
```
