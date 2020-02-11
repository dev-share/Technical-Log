cd bin
sh kafka-server-stop.sh
sh kafka-server-start.sh ../config/server.properties > /dev/null 2>&1 &
