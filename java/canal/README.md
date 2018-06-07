# Canal安装与部署

1. 开启MySQL的binlog功能并配置为row模式(my.ini)

```ini
  #log bin
  log-bin=mysql-bin
  #binlog mode: ROW mode
  binlog-format=ROW
  #config mysql replaction privileges，mustn't repeat with slaveId of canal
  server-id=1
```
2. 下载Canal(下载地址:  https://github.com/alibaba/canal/releases/download/canal-1.0.25/canal.deployer-1.0.25.tar.gz   )并解压至指定路径

3. 配置canal
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
