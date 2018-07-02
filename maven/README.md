1. maven配置seetings-1.0.0
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd"></settings>
```
2. maven配置seetings-1.1.0
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd"></settings>
```        
详细参考：http://maven.apache.org/xsd/

3. maven命令
1)下载源码
---
```mvn
mvn dependency:sources -U -fae
```
2)下载jar包或依赖包
```bash
mvn clean install -U -Dmaven.test.skip=true

mvn help:system
```
3)发布jar包
```bash
mvn clean deploy -U -X  -Dmaven.test.skip=true
```
