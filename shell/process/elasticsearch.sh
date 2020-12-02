cd /opt/deploy/data/elasticsearch
kill -9 $(ps -ef|grep -v grep|grep elasticsearch|awk '{print $2}')
sleep 3
#rm -rf data/*
rm -rf logs/*
sudo -u elasticsearch -p elasticsearch bin/elasticsearch -d
