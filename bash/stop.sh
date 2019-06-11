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

ENV_ACTIVE=`active $*`
if [ ! -n "${ENV_ACTIVE}" ] ; then
	ENV_ACTIVE=`sed '/spring.profiles.active/!d;s/.*=//' $CONF | tr -d '\r'`
fi

SPRING_BOOT=target-manager-service
LOG_CONF=${BASE_PATH}/config/log4j2.properties
APP_NAME=${SPRING_BOOT}-${ENV_ACTIVE}
CONF=${BASE_PATH}/config/application-${ENV_ACTIVE}.properties
HTTP_PORT=`sed '/server.port/!d;s/.*=//' $CONF | tr -d '\r'`
SERVER_IP=`ipconf`

if [ -n "${APP_NAME}" ] ; then
	kid=`ps -ef |grep ${APP_NAME}|grep -v grep|awk '{print $2}'`
	echo "[${SERVER_IP}]pid[$kid] from `uname` system process!"
fi

if [ -n "${kid}" ]; 
then
	echo "${APP_NAME} pid:${kid}"
	kill -9 ${kid}
	echo ----------------------------${app_name} STOPED SUCCESS------------------------------------
else
	echo "${APP_NAME} pid isn't exist or has STOPED !"
fi
