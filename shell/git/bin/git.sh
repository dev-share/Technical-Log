
#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin

function usage() {
    echo "Usage: git.sh [OPTIONS]"
    echo "Options:"
    echo " --1 project_name [require]"
    echo " --2 project_version [require]"
    echo " --3 git_tag_msg [require]"
    echo " --4 git_branch [option,default:develop]"
    echo " --5 project_group [option,default:default]"
    echo " --6 git_url [option,default:]"
    exit
}

if [ -z $1 ] ; then
   usage
elif [  -z $2 ] ; then
   usage
elif [  -z $3 ] ; then
   usage
fi

project=$1
version=$2
msg=$3
branch=${4:-"develop"}
group=${5:-"default"}
gurl=$6

source ${BIN_PATH}/sshkey

WS_PATH=${WS_PATH}/$group
PROJECT_PATH=${WS_PATH}/$project
if [ -z $PROJECT_PATH ] ; then
      if [  ! -f ${PROJECT_PATH}/.git/config -a  -z $gurl ] ; then
   	echo "please config git url param 6"
   	exit 1
      else
	${BIN_PATH}/clone.sh $branch $gurl $project
      fi
fi

cd ${WS_PATH}/$project
tag=$version
if [[ $version != v* ]];then
	tag=v$version
fi

slave=$branch
master="master"
if [[ $slave != "develop" ]] ; then
	master="master_$slave"
fi

git checkout $slave
${BIN_PATH}/pull.sh
${BIN_PATH}/tag.sh $tag $msg
git checkout $master
echo "--->>project:$project,master:$master,slave:$slave,version:$version,msg:$msg---"
${BIN_PATH}/merge.sh $tag $slave $master
git checkout $slave
${BIN_PATH}/pom.sh $project $version $group
