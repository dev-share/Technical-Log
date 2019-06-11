#!/bin/bash
export BUILD_ID=spring_boot_build_`date "+%s%3N"`

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

function ipconf() {
    case "`uname`" in
        Darwin)
         server_ip=`echo "show State:/Network/Global/IPv4" | scutil | grep PrimaryInterface | awk '{print $3}' | xargs ifconfig | grep inet | grep -v inet6 | awk '{print $2}'`
         ;;
        *)
         server_ip=`ip route get 1 | awk '{print $NF;exit}'`
         ;;
  esac
  echo ${server_ip}
}


function ipconf() {
    case "`uname`" in
        Darwin)
         server_ip=`echo "show State:/Network/Global/IPv4" | scutil | grep PrimaryInterface | awk '{print $3}' | xargs ifconfig | grep inet | grep -v inet6 | awk '{print $2}'`
         ;;
        *)
         server_ip=`ip route get 1 | awk '{print $NF;exit}'`
         ;;
  esac
  echo ${server_ip}
}

function jenv(){
	eval A='('$*')'
	for i in ${!A[*]}
	do
		OPT=${A[$i]}
		if [[ $OPT == -D* ]];then
			JAVA_OPTS=" $JAVA_OPTS $OPT"
			unset A[$i]
		fi
	done
	echo ${JAVA_OPTS}
}

function active(){
	eval A='('$*')'
	for i in ${!A[*]}
	do
		OPT=${A[$i]}
		if [[ $OPT == -Dspring.profiles.active* ]];then
			ENV_ACTIVE=${OPT##*=}
		fi
	done
	echo ${ENV_ACTIVE}
}

CONF=${BASE_PATH}/config/application.properties
ENV_ACTIVE=`active $*`
if [ -n "${ENV_ACTIVE}" ] ; then
	echo "ENV:${ENV_ACTIVE}"
	if [ -e ${BASE_PATH}/config/application-${ENV_ACTIVE}.properties ] ; then
		sed -i "s#^spring.profiles.active=.*#spring.profiles.active=$(echo ${ENV_ACTIVE})#g" $CONF
	fi
fi

SPRING_BOOT=target-manager-service
LOG_CONF=${BASE_PATH}/config/log4j2.properties
ENV_ACTIVE=`sed '/spring.profiles.active/!d;s/.*=//' $CONF | tr -d '\r'`
echo "ENV_ACTIVE:${ENV_ACTIVE}"
APP_NAME=${SPRING_BOOT}-${ENV_ACTIVE}
CONF=${BASE_PATH}/config/application-${ENV_ACTIVE}.properties
HTTP_PORT=`sed '/server.port/!d;s/.*=//' $CONF | tr -d '\r'`
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
	occupy=`netstat -ano|grep -v grep|grep ${HTTP_PORT}|grep 'LISTEN'`
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

if [ "$JAVA_HOME" != "" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi
JAVA_ENV="`jenv $*` -server -Xms512M -Xmx512M -Xss1m"
JAVA_OPTS="$JAVA_ENV -DAPP_NAME=${APP_NAME} -Dbase.path=${BASE_PATH} -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError "
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
	echo ------$JAVA_OPTS
	cd ${BASE_PATH}
	for file in "${BASE_PATH}"/*.jar
	do
	    file=${file##*/}
	    filename=${file%.*}
	    echo -----------------file=${file},filename=${filename}------------------
	    if [[ $filename =~ $SPRING_BOOT ]]; then
	    	app=$file
	    	echo app jar:$app
	    	break;
	    fi
	done
	
	CLASSPATH="${BASE_PATH}/config:$CLASSPATH";
	
	echo ${APP_NAME} Starting ...
	$JAVA $JAVA_OPTS -classpath .:$CLASSPATH -jar $app ${A[*]} --logging.config=$LOG_CONF >/dev/null 2>${BASE_PATH}/logs/error.log &
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