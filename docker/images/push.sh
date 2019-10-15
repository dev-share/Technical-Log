if [ -z $1 ] ; then
   echo "No args."
   exit 1
fi

version='1.0.0'

if [ -n "$2" ] ; then
   version=$2
fi

path=$1
name=${path##*/}
echo $name-$version
docker_name=$name:$version

cd $1

if [ -n "$(docker ps -a|grep -v grep|grep ${docker_name}|awk '{print $1}')" ] ; then
	docker stop $(docker ps -a|grep -v grep|grep ${docker_name}|awk '{print $1}')
	docker rm -f $(docker ps -a|grep -v grep|grep ${docker_name}|awk '{print $1}')
fi
if [ -n "$(docker images -a|grep -v grep|grep $name|grep $version|awk '{print $3}')" ] ; then
	docker rmi -f $(docker images -a|grep -v grep|grep $name|grep $version|awk '{print $3}')
fi

docker build -t ${docker_name} --rm .

