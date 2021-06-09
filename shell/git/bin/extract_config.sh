#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
PROJECT=m7it
if [ -n "$1" ] ; then
   PROJECT=$1
fi
PROJECT_WS=${WS_PATH}/${PROJECT}
PROJECT_CONF=${BASE_PATH}/tmp/${PROJECT}
if [ ! -d ${PROJECT_WS} ] ; then
	echo "----[error]${PROJECT_WS} is not workspace----"
fi
echo "------Copy Project Config[start]------"
for name in `ls ${PROJECT_WS}`
do
	    if [[ $name == *web ]];then
	    	continue;
	    fi
	    echo -----------------$name
	    project_path=${PROJECT_CONF}/$name
	    mkdir -p ${project_path}
	    rm -rf ${project_path}/*
	    cp -vr ${PROJECT_WS}/$name/src/main/resources/application*.properties ${project_path}
done
echo "------Copy Project Config[finish]------"
tree ${PROJECT_CONF}
