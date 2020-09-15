#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin

source ${BIN_PATH}/sshkey
rm -rf ${WS_PATH}/dbs/* && mkdir -p ${WS_PATH}/dbs && cd ${WS_PATH}/dbs
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

rm -rf ${WS_PATH}/cps/* && mkdir -p ${WS_PATH}/cps && cd ${WS_PATH}/cps
${BIN_PATH}/clone.sh develop http://${server}/ICS/HistorySvc.git history-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/HistoryPipe.git history-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/RealtimeSvc.git realtime-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/RealtimePipe.git realtime-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/RealtimeAnalysisSvc.git realtime-analysis-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/AlarmPipe.git alarm-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/AlarmSvc.git alarm-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/AuditLogSvc.git audit-log-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/AuditLogPipe.git audit-log-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/HmiEditorSvc.git hmi-editor-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/HmiEditorWeb.git hmi-editor-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/ModelerSvc.git modeler-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/ModelPipe.git model-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/PlatformManagerSvc.git platform-manager-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/PlatformManagerWeb.git platform-manager-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_Authority_Service.git sf-authority-service
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_Authority_Web.git sf-authority-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_System_Service.git sf-system-service
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_System_Web.git sf-system-web
ls -lrth