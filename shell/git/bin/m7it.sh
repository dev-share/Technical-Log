#!/bin/bash
path="${BASH_SOURCE-$0}"
path="$(dirname "${path}")"
path="$(cd "${path}";pwd)"
base=${path}/..
BASE_PATH="$(cd "${base}";pwd)"
WS_PATH=${BASE_PATH}/workspace
BIN_PATH=${BASE_PATH}/bin
PROJECT=m7it
PROJECT_WS=${WS_PATH}/${PROJECT}
ls -lrth

source ${BIN_PATH}/sshkey

mkdir -p ${PROJECT_WS} && rm -rf ${PROJECT_WS}/* &&  cd ${PROJECT_WS}

${BIN_PATH}/clone.sh develop http://${server}/M7IT/GRpcSvc.git	grpc-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/RealtimeSvc.git	realtime-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/RealtimePipe.git	realtime-pipe
${BIN_PATH}/clone.sh develop http://${server}/M7IT/RealtimeAnalysisSvc.git	realtime-analysis-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/HistorySvc.git	history-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/HistoryPipe.git	history-pipe
${BIN_PATH}/clone.sh develop http://${server}/M7IT/AlarmPipe.git	alarm-pipe
${BIN_PATH}/clone.sh develop http://${server}/M7IT/AlarmSvc.git	alarm-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/ModelerSvc.git	modeler-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/ModelerPluginSvc.git	modeler-plugin-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/ModelerWeb.git	modeler-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/TagSchemaPipe.git	tagschema-pipe
${BIN_PATH}/clone.sh develop http://${server}/M7IT/TagSchemaSvc.git	tagschema-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/AuditLogSvc.git	audit-log-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/AuditLogPipe.git	audit-log-pipe
${BIN_PATH}/clone.sh develop http://${server}/M7IT/PlatformManagerSvc.git	platform-manager-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/PlatformManagerWeb.git	platform-manager-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/ApplicationDesignWeb.git	application-design-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SF_Authority_Service.git	sf-authority-service
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SF_Authority_Web.git	sf-authority-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SF_System_Service.git	sf-system-service
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SF_System_Web.git	sf-system-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/HmiEditorSvc.git	hmi-editor-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/HmiEditorWeb.git	hmi-editor-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SymbolEditorWeb.git	symbol-editor-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/DockerSvc.git	docker-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/MqttIO.git	mqtt-io
${BIN_PATH}/clone.sh develop http://${server}/M7IT/KafkaIO.git	kafka-io
${BIN_PATH}/clone.sh develop http://${server}/M7IT/HttpIO.git		http-io
${BIN_PATH}/clone.sh develop http://${server}/M7IT/OpcuaIO.git	opc-ua-io
${BIN_PATH}/clone.sh develop http://${server}/M7IT/UdpIo.git		udp-io
${BIN_PATH}/clone.sh develop http://${server}/M7IT/OperationManuaWeb.git	operation-manua-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/NodeRedPlugin.git	node-red
${BIN_PATH}/clone.sh develop http://${server}/M7IT/ServerHeart.git	server-heart
${BIN_PATH}/clone.sh develop http://${server}/M7IT/python-lab.git	python-lab

${BIN_PATH}/clone.sh develop http://${server}/M7IT/MonitorSvc.git	monitor-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/MonitorWeb.git	monitor-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FlowChartSvc.git	flow-chart-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FlowChartWeb.git	flow-chart-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FlowableManageSdk.git        flowable-manage-sdk
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FlowableManageSvc.git	flowable-manage-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FlowChartPlugin.git	flow-chart-plugin
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FlowChartSvc.git	flow-chart-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FormGeneratorWeb.git	form-generator-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/FormManageSvc.git		form-manage-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/InformationSvc.git	information-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/RulesComposerSvc.git	rules-composer-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/RulesEngineSvc.git	rules-engine-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/RulesEngineWeb.git	rules-engine-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SSOSvc.git	sso-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/SSOWeb.git	sso-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/WorkFlowWeb.git	work-flow-web
${BIN_PATH}/clone.sh develop http://${server}/M7IT/WorkflowUserSideWeb.git      work-flow-user-side-web

${BIN_PATH}/clone.sh develop http://${server}/M7IT/EventPipe.git	event-pipe
${BIN_PATH}/clone.sh develop http://${server}/M7IT/EventSvc.git	event-svc
${BIN_PATH}/clone.sh develop http://${server}/M7IT/ReportSvc.git	report-svc

${BIN_PATH}/clone.sh develop http://${server}/M7IT/WebCommon.git	web-common

rm -rf ../*.tar.gz
tar -zcvf ../m7it.tar.gz *

ls -1

