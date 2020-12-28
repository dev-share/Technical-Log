#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin

function usage() {
    echo "Usage: pom.sh [OPTIONS]"
    echo "Options:"
    echo " --1 project_name [require]"
    echo " --2 project_version [require]"
    echo " --3 project_group [option,default:default]"
    exit
}

if [ -z $1 ] ; then
   usage
fi

if [ -z $2 ] ; then
   usage
fi

if [ -z $3 ] ; then
   usage
fi

project=$1
version=$2
group=${3:-"default"}
if [[ $version == v* ]];then
   version=${version#*v}
fi
cd ${WS_PATH}/$group/$project
major=${version%.*}
minor=${version##*.}
let minor=${minor}+1
latest="${major}.${minor}"
echo ---major:${major},minor:${minor},latest:${latest}------
tmp=`ls|grep -v grep|grep "pom.xml"`
if [ -n "$tmp" ] ; then
	sed -i "s/<version>$(echo $version)</<version>$(echo $latest)</" pom.xml
	${BIN_PATH}/push.sh $latest
fi
