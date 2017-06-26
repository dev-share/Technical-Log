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
