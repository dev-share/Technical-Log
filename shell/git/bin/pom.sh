#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
if [ -z $1 ] ; then
   echo " no git dir"
   exit 1
fi

if [ -z $2 ] ; then
   echo " no version"
   exit 1
fi
project=$1
version=$2
if [[ $version == v* ]];then
   version=${version#*v}
fi
cd ${WS_PATH}/$project
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
