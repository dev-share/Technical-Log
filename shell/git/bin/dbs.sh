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

${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_TargetCalculate_Service.git target-calculate-service
${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_TargetManger_Service.git target-manager-service
${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_ModelManager_Service.git sf-modelmanager-service
${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_GatewayProxy_Service.git sf-gatewayproxy-service
${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_History_Service.git sf-history-service
${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_RealTime_Service.git sf-realtime-service
${BIN_PATH}/clone.sh develop http://${server}/SmartFactory/SF_TargetManger_Web.git target-manager-web
${BIN_PATH}/clone.sh develop http://${server}/DBS/DBS_Cockpit_Web.git dbs-cockpit-web
${BIN_PATH}/clone.sh develop http://${server}/DBS/DBS_DecisionSystem_Web.git dbs-decision-system-web
${BIN_PATH}/clone.sh dbs http://${server}/SmartFactory/SF_Authority_service.git system-authority-service
${BIN_PATH}/clone.sh dbs http://${server}/SmartFactory/SF_System_Manager.git system-manager-service
${BIN_PATH}/clone.sh dbs http://${server}/SmartFactory/SF_AUTH_Web.git system-authority-web
${BIN_PATH}/clone.sh dbs http://${server}/SmartFactory/SF_System_Manager_web.git system-manager-web
ls -lrth

