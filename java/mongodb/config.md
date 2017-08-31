1.下载安装

2.MongoDB配置(mongodb.conf)

#数据库路径

dbpath=D:\MongoDB\data

#日志输出文件路径

logpath=D:\MongoDB\logs\mongodb.log

#错误日志采用追加模式，配置这个选项后mongodb的日志会追加到现有的日志文件，而不是从新创建一个新文件

logappend=true

#启用日志文件，默认启用

journal=true

#这个选项可以过滤掉一些无用的日志信息，若需要调试使用请设置为false

quiet=true

#端口号[默认:27017]

port=27017

3.Windows服务配置(mongodb.bat)

net stop MongoDB

sc delete MongoDB

sc create MongoDB binPath= "D:\MongoDB\bin\mongod.exe --service --config=D:\MongoDB\conf\mongodb.conf"

sc config MongoDB start= AUTO

net start MongoDB

4.用户权限配置（启动后执行mongo.exe）命令

use admin

db.createUser({user:"mongodb",pwd:"mongodb",roles:["userAdminAnyDatabase","dbAdminAnyDatabase","root"]})

5.导入导出（参考地址： http://www.cnblogs.com/xiaotengyi/p/6393972.html ）

1）mongodump导出（cd到安装目录bin下）

mongodump -h IP --port 端口 -u 用户名 -p 密码 --authenticationDatabase admin -d 数据库 -c 数据表 -o 文件存在径

比如本机操作:

A.认证: mongodump -u admin -p 123456 --authenticationDatabase admin -d test -o /home/

B.无认证:  mongodump -d test -o /home/

2)mongorestore导入（cd到安装目录bin下）

mongorestore -h IP --port 端口 -u 用户名 -p 密码 --authenticationDatabase admin -d 数据库 --drop 文件存在路径

比如本机操作:(drop清空导入)

A.认证: mongorestore -u admin -p 123456 --authenticationDatabase admin -d test /home/

B.无认证:  mongorestore -d test  /home/

3）mongoexport导出（cd到安装目录bin下）

mongoexport -h IP --port 端口 -u 用户名 -p 密码 -d 数据库 -c 表名 -f 字段 -q 条件导出 --格式(cvs,json) -o 文件名

比如本机操作:

A.认证: mongoexport -u admin -p 123456 --authenticationDatabase admin -d tes -c t_table -o /home/t_table.json --type=json

B.无认证:  mongoexport -d test -c t_table -o /home/t_table.json --type=json

4)mongoimport导入（cd到安装目录bin下）[默认json格式]

mongoimport -h IP --port 端口 -u 用户名 -p 密码 -d 数据库 -c 表名 --type 类型 --headerline --upsert --drop 文件名 

mongoimport -h IP --port 端口 -u 用户名 -p 密码 -d 数据库 -c 表名 --upsertFields 字段 --drop 文件名

mongoimport -h IP --port 端口 -u 用户名 -p 密码 -d 数据库 -c 表名 --upsert --drop 文件名 

比如本机操作:

mongoimport -d test -c students students.dat 

mongoimport -d tank -c users  --upsertFields uid,name,sex  tank/users.dat  

mongoimport -d tank -c users --type csv --headerline --file tank/users.csv 

mongoimport -d local -c gis  --type json --file likehua.data


