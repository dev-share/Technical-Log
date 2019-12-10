version=1.0.5
path=release
echo ---${version}
./push.sh ${path}/authority ${version}
./push.sh ${path}/authority-web ${version}
./push.sh ${path}/dbs-decision-system-web ${version}
./push.sh ${path}/dbs-cockpit-web ${version}
./push.sh ${path}/sf-gatewayproxy-service ${version}
./push.sh ${path}/sf-history-service ${version}
./push.sh ${path}/sf-modelmanager-service ${version}
./push.sh ${path}/sf-realtime-service ${version}
./push.sh ${path}/system-manager ${version}
./push.sh ${path}/system-manager-web ${version}
./push.sh ${path}/target-calculate-service ${version}
./push.sh ${path}/target-manager-service ${version}
./push.sh ${path}/target-manager-web ${version}
./push.sh ${path}/xxl-job-admin 2.1.1
