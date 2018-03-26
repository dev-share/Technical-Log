# Gradle配置
## 1. 下载[https://services.gradle.org/distributions/gradle-4.6-bin.zip]
## 2. 解压安装
  安装成功使用gradle -v 验证
## 3. 配置环境变量
```bash
  GRADLE_HOME=D:\Gradle
  GRADLE_USER_HOME=D:/Gradle/.gradle
```
## 4. 配置jar包管理路径
```bash
gradle -g D:/Gradle/.gradle build build
```
## 5. 配置中央仓库
创建init.gradle文件(位于安装目录/.gradle下)
```gradle
allprojects{
    repositories {
        def REPOSITORY_URL = 'https://jcenter.bintray.com/'
        all { ArtifactRepository repo ->
            if(repo instanceof MavenArtifactRepository){
                def url = repo.url.toString()
                if (url.startsWith('https://repo1.maven.org/maven2')) {
                    project.logger.lifecycle "Repository ${repo.url} replaced by $REPOSITORY_URL."
                    remove repo
                }
            }
        }
        maven {
            url REPOSITORY_URL
        }
	maven { url 'http://central.maven.org/maven2/' }
	maven { url 'https://oss.sonatype.org/content/repositories/releases/' }
	maven { url 'https://oss.sonatype.org/content/groups/public/' }
	maven { url 'http://maven.oschina.net/content/groups/public/' }
	maven { url 'http://maven.aliyun.com/nexus/content/repositories/central/' }
	maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
	maven { url 'http://clojars.org/repo/' }
	maven { url 'http://repo.spring.io/plugins-release/' }
	maven { url 'http://repo.spring.io/libs-milestone/' }
	maven { url 'http://repo.spring.io/milestone/' }
	maven { url 'https://maven.atlassian.com/content/repositories/atlassian-public/' }
	maven { url 'https://maven.atlassian.com/3rdparty/' }
	maven { url 'https://repository.jboss.org/nexus/content/repositories/releases/' }
	maven { url 'https://maven-eu.nuxeo.org/nexus/content/repositories/public-releases/' }
	maven { url 'http://maven.xwiki.org/releases/' }
	maven { url 'https://repository.apache.org/content/repositories/releases/' }
	maven { url 'http://repo.hortonworks.com/content/repositories/releases/' }
	maven { url 'https://maven.repository.redhat.com/ga/' }
	maven { url 'https://repository.cloudera.com/content/repositories/releases/' }
	maven { url 'https://artifacts.alfresco.com/nexus/content/repositories/public/' }
	maven { url 'http://dist.wso2.org/maven2/' }
	maven { url 'https://maven.java.net/content/repositories/releases/' }
	maven { url 'http://repo.boundlessgeo.com/main/' }
	maven { url 'http://repo.opennms.org/maven2/' }
	maven { url 'https://repository.jboss.org/nexus/content/repositories/ea/' }
	maven { url 'http://uk.maven.org/maven2/' }
	maven { url 'https://devtools.jahia.com/nexus/content/groups/maven-jahia-org/' }
	maven { url 'http://maven.elasticsearch.org/releases/' }
	maven { url 'https://artifacts.elastic.co/maven/' }
    }
}
```
