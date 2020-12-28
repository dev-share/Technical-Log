#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
PROJECT=dbs
PROJECT_WS=${WS_PATH}/${PROJECT}
ls -lrth

source ${BIN_PATH}/sshkey

mkdir -p ${PROJECT_WS} && rm -rf ${PROJECT_WS}/* &&  cd ${PROJECT_WS}

ls -lrth

