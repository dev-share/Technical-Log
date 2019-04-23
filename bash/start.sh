#!/bin/bash
export BUILD_ID=spring_boot_build_id

path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
case "`uname`" in
    Linux)
		linux=true
		;;
	*)
		linux=false
		;;
esac
APP_NAME=test
CONF=${BASE_PATH}/config/application.properties
LOG_CONF=${BASE_PATH}/config/log4j2.properties
ENV_ACTIVE=`sed '/spring.profiles.active/!d;s/.*=//' $CONF | tr -d '\r'`
CONF=${BASE_PATH}/config/application-${ENV_ACTIVE}.properties
HTTP_PORT=`sed '/server.port/!d;s/.*=//' $CONF | tr -d '\r'`
SERVER_IP=`ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v 0.0.0.0|grep -v 192.168|grep -v inet6|awk '{print $2}'|tr -d "addr:"`

if [ -n "${HTTP_PORT}" -a ! -z "${HTTP_PORT}" ] ; then
	occupy=`netstat -ano|grep -v grep|grep ${HTTP_PORT}`
	if [ -n "$occupy" ] ; then
		echo [${SERVER_IP}] Port[${HTTP_PORT}] is occupied!
		exit 1
	fi
fi

if [ -n "${APP_NAME}" ] ; then
	kid=`ps -ef |grep ${APP_NAME}|grep -v grep|awk '{print $2}'`
	echo [${SERVER_IP}]pid[$kid] from `uname` system process!
fi

if [ -n "$kid" ] ; then
	echo [`hostname -i`|`uname`] ${APP_NAME} process [$kid] is Running!
	kill -9 $kid;
fi

if [ ! -d ${BASE_PATH}/logs ] ; then
	mkdir -p ${BASE_PATH}/logs
fi

if [ ! -d ${BASE_PATH}/data ] ; then
	mkdir -p ${BASE_PATH}/data
fi

if [ "$JAVA_HOME" != "" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi
JAVA_ENV="-server -Xms2g -Xmx2g -Xss1m "
JAVA_OPTS="$JAVA_ENV -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError "

eval A='('$*')'
for i in ${!A[*]}
do
	OPT=${A[$i]}
	if [[ $OPT == -D* ]];then
		JAVA_OPTS=" $JAVA_OPTS $OPT"
		unset A[$i]
	fi
done

for i in "${BASE_PATH}"/lib/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done

if [ -e $CONF -a -d ${BASE_PATH}/logs ]
then
	set timeout 60
	echo -------------------------------------------------------------------------------------------
	cd ${BASE_PATH}
	chmod +x */*
	for file in "${BASE_PATH}"/*.jar
	do
	    file=${file##*/}
	    filename=${file%.*}
	    echo -----------------file=${file},filename=${filename}------------------
	    if [[ $filename =~ $APP_NAME ]]; then
	    	app=$file
	    	echo app jar:$app
	    	break;
	    fi
	done
	
	echo ${APP_NAME} Starting ...
	$JAVA $JAVA_OPTS -Dapp.name=${APP_NAME} -Dbase.path=${BASE_PATH} -jar $app ${A[*]} --spring.config.location=$CONF --logging.config=$LOG_CONF >/dev/null 2>&1 &
	echo $! > $PID
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
		echo "=========>`hostname`(${SERVER_IP}):${APP_NAME}[pid:${kpid}]STARTUP SUCCESS!"
	else
		echo "=========>`hostname`(${SERVER_IP}):${APP_NAME}[pid:${kpid}]STARTUP FAIL!"
		exit 1
	fi
	echo -------------------------------------------------------------------------------------------
else
	echo "${APP_NAME} config($CONF) Or logs direction is not exist,please create first!"
	exit 1
fi
