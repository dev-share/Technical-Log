#!/bin/bash
BASE_PATH=/home/gitlab
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
rm -rf ${WS_PATH}/*
source ${BIN_PATH}/sshkey
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_TargetCalculate_Service.git target-calculate-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_TargetManger_Service.git target-manager-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_ModelManager_Service.git sf-modelmanager-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_GatewayProxy_Service.git sf-gatewayproxy-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_History_Service.git sf-history-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_RealTime_Service.git sf-realtime-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/SmartFactory/SF_TargetManger_Web.git target-manager-web
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/DBS/DBS_Cockpit_Web.git dbs-cockpit-web
cd ${WS_PATH}
${BIN_PATH}/clone.sh develop http://172.21.32.31/DBS/DBS_DecisionSystem_Web.git dbs-decision-system-web
cd ${WS_PATH}
${BIN_PATH}/clone.sh dbs http://172.21.32.31/SmartFactory/SF_Authority_service.git system-authority-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh dbs http://172.21.32.31/SmartFactory/SF_System_Manager.git system-manager-service
cd ${WS_PATH}
${BIN_PATH}/clone.sh dbs http://172.21.32.31/SmartFactory/SF_AUTH_Web.git system-authority-web
cd ${WS_PATH}
${BIN_PATH}/clone.sh dbs http://172.21.32.31/SmartFactory/SF_System_Manager_web.git system-manager-web
