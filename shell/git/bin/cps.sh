#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
PROJECT=cps
PROJECT_WS=${WS_PATH}/${PROJECT}
ls -lrth

source ${BIN_PATH}/sshkey

mkdir -p ${PROJECT_WS} && rm -rf ${PROJECT_WS}/* &&  cd ${PROJECT_WS}

${BIN_PATH}/clone.sh develop http://${server}/ICS/GRpcSvc.git grpc-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/RealtimeSvc.git realtime-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/RealtimePipe.git realtime-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/RealtimeAnalysisSvc.git realtime-analysis-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/HistorySvc.git history-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/HistoryPipe.git history-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/AlarmPipe.git alarm-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/AlarmSvc.git alarm-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/ModelerSvc.git modeler-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/ModelerWeb.git modeler-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/TagSchemaPipe.git tagschema-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/TagSchemaSvc.git tagschema-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/AuditLogSvc.git audit-log-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/AuditLogPipe.git audit-log-pipe
${BIN_PATH}/clone.sh develop http://${server}/ICS/PlatformManagerSvc.git platform-manager-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/PlatformManagerWeb.git platform-manager-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_Authority_Service.git sf-authority-service
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_Authority_Web.git sf-authority-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_System_Service.git sf-system-service
${BIN_PATH}/clone.sh develop http://${server}/ICS/SF_System_Web.git sf-system-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/HmiEditorSvc.git hmi-editor-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/HmiEditorWeb.git hmi-editor-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/SymbolEditorWeb.git symbol-editor-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/DockerSvc.git docker-svc
${BIN_PATH}/clone.sh develop http://${server}/ICS/MqttIO.git mqtt-io
${BIN_PATH}/clone.sh develop http://${server}/ICS/KafkaIO.git kafka-io
${BIN_PATH}/clone.sh develop http://${server}/ICS/HttpIO.git http-io
${BIN_PATH}/clone.sh develop http://${server}/ICS/OpcuaIO opc-ua-io
${BIN_PATH}/clone.sh develop http://${server}/ICS/OperationManuaWeb operation-manua-web
${BIN_PATH}/clone.sh develop http://${server}/ICS/NodeRedPlugin.git node-red
${BIN_PATH}/clone.sh develop http://${server}/ICS/ServerHeart.git server-heart
${BIN_PATH}/clone.sh develop http://${server}/ICS/python-lab python-lab

ls -lrth
