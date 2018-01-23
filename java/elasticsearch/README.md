Elasticsearch及其管理工具或插件安装配置

一．Elasticsearch安装配置

1.安装配置

1）配置ES_HOME环境变量，例如:  ES_HOME=C:\Java\elasticsearch-5.6.0

2）增加Path环境变量，例如：Path=...;%ES_HOME%\bin;

3）启动 call elasticsearch 或 call elasticsearch -d

4）http://localhost:9200

2.插件配置(https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.6.0.zip)

1）插件安装（X-Pack为例）

elasticsearch-plugin install file:///E:/x-pack-5.6.0.zip

2）卸载

elasticsearch-plugin remove x-pack --purge

手动删除config/x-pack目录

3）启动

默认用户密码：elastic/changeme(可以通过kibana改变密码)

二．管理工具及其插件

1.kibana安装配置

1）配置

(1)配置ES_HOME环境变量，例如:EK_HOME=C:\Java\kibana-5.6.0

(2)增加Path环境变量,例如：Path=...;%EK_HOME%\bin;

(3)启动 call kibana 或 call kibana -d

(4)http://localhost:5601

2）插件安装

(1)插件安装（X-Pack为例）

kibana-plugin install file:///E:/x-pack-5.6.0.zip

(2)卸载

kibana-plugin remove x-pack

(3)启动

默认用户密码：elastic/changeme(可以通过kibana改变密码)

2. logstash安装配置

1）配置

(1)配置ES_HOME环境变量，例如:EL_HOME=C:\Java\logstash-5.6.0

(2)增加Path环境变量,例如：Path=...;%EL_HOME%\bin;

(3)logstash.conf

input {
  stdin { type => stdin }
  tcp {
    port => 5544
  }
  udp {
    port => 5544
  }
}
filter {
  date {
    match => [ "timestamp" , "yyyy-MMM-dd HH:mm:ss Z" ]
  }
}
output {
  stdout { codec => rubydebug }
  elasticsearch {
    hosts => ["localhost:9200"]
  }
}

(4)启动 call logstash -f logstash.conf 或 call logstash -d -f logstash.conf

(5)http://localhost:5544

2）插件安装

(1)插件安装（X-Pack为例）

logstash-plugin install file:///E:/x-pack-5.6.0.zip

(2)卸载

logstash-plugin remove x-pack

(3)启动

默认用户密码：elastic/changeme(可以通过kibana改变密码)




*注：D:\WebServer\ELK\6.1.x>call elasticsearch-6.1.2/bin/elasticsearch-plugin install file:///D:/WebServer/ELK/6.1.x/x-pack-6.1.2.zip*
