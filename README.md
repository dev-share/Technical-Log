# Complex-Project
this project include java,javascript,java web,html5+css3,jsp and so on.

一、JS插件

1.JS日历插件包含中国农历以及二十四节气

二.Java技术

1.共享工具

1）日期格式化

2）http请求[http请求支持认证机制(用户名密码校验)]

2.支持Canal服务(MySQL的binlog监控)

3.支持Cassandra连接及其接口服务封装

4.支持传统数据库JDBC或Druid方式接口封装

5.支持数据仓库JDBC或Druid方式接口封装

7.支持Elasticsearch的Http API或Java API底层封装

8.Maven配置以及丰富Maven仓库(setting.xml|pom.xml)

9.支持Kafka消息队列服务

10.支持MongoDB连接及其接口服务封装

三.Linux命令

1.创建目录

sudo mkdir /home/test

2.文件夹赋予读写执行权限

chown -R user /home/elk //读写权

chmod +x /home/elk //可执行权限

3.解压文件

sudo tar -zxvf /home/user/jdk-8u144-linux-x64.tar.gz -C /home

4.跨服务器拷贝

scp -r /home/elk root@192.168.1.2:/home

根据提示输入密码

5.tracert追踪

tracert -d www.google.com
