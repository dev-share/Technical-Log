#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin

project=${1:-"all"}
version=${2:-"1.0.0"}
msg=${3:-"新版本"}
branch=${4:-"develop"}
group=${5:-"default"}

WS_PATH=${WS_PATH}/$group

if [[ $project != "all" ]] ; then
	${BIN_PATH}/git.sh ${project} $version $msg $branch $group $6
else
	${BIN_PATH}/curl.sh
	echo ---${WS_PATH}---$version---$msg
	for project in `ls ${WS_PATH}`
	do
	 	GIT_PATH=${WS_PATH}/${project}
	 	if [ -d ${GIT_PATH}  ] ; then
        			${BIN_PATH}/git.sh ${project} $version $msg $branch $group
	 	fi
	 	#break;
	done
fi

						
