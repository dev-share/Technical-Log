if [ -z $1 ] ; then
   echo "No args."
   exit 1
fi

version='1.0.0'

if [ -n "$2" ] ; then
   version=$2
fi

name=$1
echo $name-$version
docker_name=$name:$version
docker_register=172.21.32.31:5000
cd $1

if [ -n "$(docker ps -a|grep -v grep|grep ${docker_register}/${docker_name}|awk '{print $1}')" ] ; then
	docker stop $(docker ps -a|grep -v grep|grep ${docker_register}/${docker_name}|awk '{print $1}')
	docker rm -f $(docker ps -a|grep -v grep|grep ${docker_register}/${docker_name}|awk '{print $1}')
fi
if [ -n "$(docker images -a|grep -v grep|grep ${docker_register}/$name|grep $version|awk '{print $3}')" ] ; then
	docker rmi -f $(docker images -a|grep -v grep|grep ${docker_register}/$name|grep $version|awk '{print $3}')
fi

docker build -t ${docker_register}/${docker_name} --rm .
docker push ${docker_register}/${docker_name}
