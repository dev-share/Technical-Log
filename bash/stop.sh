#!/bin/bash
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
