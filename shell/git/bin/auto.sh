#!/bin/bash
BASE_PATH=/home/gitlab
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin

version=1.0.9
msg="提测强化版"
${BIN_PATH}/curl.sh
for dir in `ls ${WS_PATH}`
do
      GIT_PATH=${BASE_PATH}/${dir}
      if [ -d ${GIT_PATH}  ] ; then
         ${BIN_PATH}/git.sh ${GIT_PATH} $version $msg
      fi
done
						
