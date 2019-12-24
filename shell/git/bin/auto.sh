#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
if [ -z $1 ] ; then
	version=1.0.9
else
	version=$1
fi

if [ -z $2 ] ; then
	msg="提测新需求版"
else
	msg=$2
fi

${BIN_PATH}/curl.sh
echo ---${WS_PATH}---$version---$msg
for project in `ls ${WS_PATH}`
do
	 GIT_PATH=${WS_PATH}/${project}
	 if [ -d ${GIT_PATH}  ] ; then
        	${BIN_PATH}/git.sh ${project} $version $msg
	 fi
	 #break;
done
						
