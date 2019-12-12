
#!/bin/bash
BASE_PATH=/home/gitlab
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

if [ -z $3 ] ; then
   echo " no msg"
   exit 1
fi
project=$1
version=$2
msg=$3
cd ${WS_PATH}/$project
tag=$version
if [[ $version != v* ]];then
	tag=v$version
fi

source ${BIN_PATH}/sshkey
${BIN_PATH}/pull.sh
${BIN_PATH}/tag.sh $tag $msg
slave="develop"
master="master"
tmp="`git branch -a|grep -v grep|grep dbs`"
if [ -n "$tmp" ] ; then
	slave="dbs"
	master="master_dbs"
fi
git checkout $master
${BIN_PATH}/merge.sh $tag $slave $master
