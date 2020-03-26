version='1.0.3-12'
name=hollysys-node-red
docker_name=$name:$version
docker_register=172.21.32.102:5000
if [ -n "$(docker images -a|grep -v grep|grep ${docker_register}/$name|grep $version|awk '{print $3}')" ] ; then
	docker rmi -f $(docker images -a|grep -v grep|grep ${docker_register}/$name|grep $version|awk '{print $3}')
fi
docker build --build-arg APP_VERSION=v$version -t ${docker_register}/${docker_name} --rm .
docker push ${docker_register}/${docker_name}
