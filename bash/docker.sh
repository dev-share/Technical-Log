#!/bin/bash
export BUILD_ID=docker_`date "+%s%3N"`
sh restart.sh

while(true);
do
	echo "docker restart target-manager-service"
done;
