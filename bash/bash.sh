cd deploy
mvn ${MAVEN_CONF} assembly:assembly -P ${env}

publish_dir=${Publish_Dir}
isbak=${bak}

if [ ! -d ${publish_dir} ] ; then
	mkdir -p ${publish_dir}
    isbak=false;
fi

path=${publish_dir}/..
base_path="$(cd "${path}";pwd)"
bak_dir=${base_path}/emgw-gateway_`date "+%Y%m%d"`_bak/`date "+%Y-%m-%dT%H"`

if ${isbak}; then
	echo "${isbak},${bak_dir}"
	if [ ! -d ${bak_dir} ] ; then
        mkdir -p ${bak_dir}
    else
        rm -rf ${bak_dir}/*
    fi
	cp -r ${publish_dir}/* ${bak_dir}/
fi

rm -rf ${publish_dir}/*

cp -r target/*.tar.gz ${publish_dir}

cd ${publish_dir}

tar -zxvf *.tar.gz

cp -r ${publish_dir}/emgw-gateway/* ${publish_dir}
rm -rf ${publish_dir}/emgw-gateway*

dos2unix *.sh
chmod +x *.sh



#!/bin/expect
set timeout 10
set host $host
set username $username
set password $password
set publish_dir $Publish_Dir
set cross $cross

if ! $cross; then
	exit 1
fi

if [ ! -d ${publish_dir} ] ; then
	exit 1
fi

if [ "$host" = "" ] ; then
	exit 1
fi

if [ "$password" != "" ] ; then
    set password "${password/!/\\!}"
else
	set password "Asdf\!@#\$${host##*.}"
fi

spawn scp -r $publish_dir/* $username@$host:$publish_dir
expect {
 "(yes/no)?"
  {
    send "yes\n"
    expect "*assword:" { send "$password\n"}
  }
 "*assword:"
  {
    send "$password\n"
  }
}