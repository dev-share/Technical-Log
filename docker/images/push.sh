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
docker_register=172.21.32.31:5000
BASE_PATH=$(echo `pwd`)
DOCKER_FILE=${BASE_PATH}/Dockerfile

if [ -f ${path}/Dockerfile ];then
	DOCKER_FILE=${BASE_PATH}/${path}/Dockerfile
fi

if [[ $name == *-web ]];then
	DOCKER_FILE=${BASE_PATH}/WebDockerfile
fi

cd ${path}

if [ -n "$(docker ps -a|grep -v grep|grep ${docker_register}/${docker_name}|awk '{print $1}')" ] ; then
	docker stop $(docker ps -a|grep -v grep|grep ${docker_register}/${docker_name}|awk '{print $1}')
	docker rm -f $(docker ps -a|grep -v grep|grep ${docker_register}/${docker_name}|awk '{print $1}')
fi
if [ -n "$(docker images -a|grep -v grep|grep ${docker_register}/$name|grep $version|awk '{print $3}')" ] ; then
	docker rmi -f $(docker images -a|grep -v grep|grep ${docker_register}/$name|grep $version|awk '{print $3}')
fi

docker build -f ${DOCKER_FILE}  --build-arg APP_VERSION=$version -t ${docker_register}/${docker_name} --rm .
docker push ${docker_register}/${docker_name}

docker_register=172.21.32.102:5000
docker build -f ${DOCKER_FILE} --build-arg APP_VERSION=$version -t ${docker_register}/${docker_name} --rm .
docker push ${docker_register}/${docker_name}
