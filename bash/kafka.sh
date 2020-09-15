rm -rf logs/*
cd bin
sh kafka-server-stop.sh
sleep 5
sh kafka-server-start.sh ../config/server.properties > /dev/null 2>&1 &
pid=$(ps -ef|grep -v grep|grep Kafka|awk '{print $2}')
sleep 1
echo "--Kafka pid:$pid"
