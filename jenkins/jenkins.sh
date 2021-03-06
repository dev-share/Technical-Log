#!/bin/bash
export BUILD_ID=build_`date "+%s%3N"`

path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
BASE_PATH="$(cd "${path}";pwd)"
case "`uname`" in
    Linux)
		linux=true
		;;
	*)
		linux=false
		;;
esac

function ipconf() {
  case "`uname`" in
       Darwin)
         server_ip=`echo "show State:/Network/Global/IPv4" | scutil | grep PrimaryInterface | awk '{print $3}' | xargs ifconfig | grep inet | grep -v inet6 | awk '{print $2}'`
       ;;
       *)
	if [[ $(lsb_release -i) == *Ubuntu ]] ; then
		server_ip=`ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v 0.0.0.0|grep -v 192.168|grep -v inet6|grep -v 0.1|awk '{print $2}'`
	else
		server_ip=`ip route get 1 | awk '{print $NF;exit}'`
	fi
       ;;
  esac
  echo ${server_ip}
}

CONF=${BASE_PATH}/conf/config.properties
SPRING_BOOT=jenkins
JENKINS_HOME=${BASE_PATH}/home
APP_NAME=${SPRING_BOOT}-docker
HTTP_PORT=`sed '/http.port/!d;s/.*=//' $CONF | tr -d '\r'`
SERVER_IP=`ipconf`

if [ -n "${APP_NAME}" ] ; then
	kid=`ps -ef |grep ${APP_NAME}|grep -v grep|awk '{print $2}'`
	echo [${SERVER_IP}]pid[$kid] from `uname` system process!
fi

if [ -n "$kid" ] ; then
	echo [${SERVER_IP}] ${APP_NAME} process [$kid] is Running!
	kill -9 $kid;
	sleep 5;
fi

if [ -n "${HTTP_PORT}" -a ! -z "${HTTP_PORT}" ] ; then
	occupy=`netstat -ano|grep -v grep|grep ':${HTTP_PORT}'|grep 'LISTEN'`
	if [ -n "$occupy" ] ; then
		echo [${SERVER_IP}] Port[${HTTP_PORT}] is occupied!
		exit 1
	fi
fi

if [ ! -d ${BASE_PATH}/logs ] ; then
	mkdir -p ${BASE_PATH}/logs
else
	rm -rf ${BASE_PATH}/logs/*
fi

if [ ! -d ${BASE_PATH}/data ] ; then
	mkdir -p ${BASE_PATH}/data
fi

if [ ! -d ${JENKINS_HOME} ] ; then
	mkdir -p ${JENKINS_HOME}
fi

if [ "$JAVA_HOME" != "" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

JAVA_ENV=" $* -server -Xms512M -Xmx512M -Xss1m"
JAVA_OPTS="$JAVA_ENV -DJENKINS_HOME=${JENKINS_HOME} -DAPP_NAME=${APP_NAME} -Dbase.path=${BASE_PATH} -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError "

if [ -e $CONF -a -d ${BASE_PATH}/logs ]
then
	set timeout 60
	echo -------------------------------------------------------------------------------------------
	echo ------$JAVA_OPTS
	cd ${BASE_PATH}
	
	CLASSPATH="${BASE_PATH}:${BASE_PATH}/config:$CLASSPATH";
	
	echo ${APP_NAME} Starting ...
	$JAVA $JAVA_OPTS -classpath .:$CLASSPATH -jar jenkins.war ${A[*]} --httpPort=${HTTP_PORT} >/dev/null 2>${BASE_PATH}/logs/error.log &
	echo ${APP_NAME} Finish ...
	DEV_LOOPS=0;
	while(true);
	do
		sleep 5;
		if $linux; then
			kpid=`ps -ef|grep java|grep "${APP_NAME}"|grep -v grep|awk '{print $2}'`
		else 
			kpid=`ps -ef|grep java|grep "${APP_NAME}"|grep -v grep|awk '{print $2}'`
		fi
		
		if [ "${kpid}" != "" ] ; then
			echo "[pid:${kpid}]OK! cost:${DEV_LOOPS}"
			break;
		fi
		
		if [ ${DEV_LOOPS} -gt 10 ] ; then
			echo "[pid:${kpid}]NO! cost:${DEV_LOOPS}"
			break;
		else
			let DEV_LOOPS=${DEV_LOOPS}+1;
		fi
	done;
	
  	if [ "${kpid}" != "" ] ; then
		echo "=========>`hostname`(${SERVER_IP}:${HTTP_PORT}):${APP_NAME}[pid:${kpid}]STARTUP SUCCESS!"
	else
		echo "=========>`hostname`(${SERVER_IP}:${HTTP_PORT}):${APP_NAME}[pid:${kpid}]STARTUP FAIL!"
		exit 1
	fi
	echo -------------------------------------------------------------------------------------------
else
	echo "${APP_NAME} config($CONF) Or logs direction is not exist,please create first!"
	exit 1
fi
