host=$host
username=$username
password=$password
publish_dir=$publish_dir
cross=$cross

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
    password="${password/!/\\!}"
    password="${password/$/\\$}"
else
	password="Asdf\!@#\$${host##*.}"
    password="${password/$/\\$}"
fi

export host
export username
export password
export publish_dir

#!/bin/expect
set timeout 30
set host $env(host)
set username $env(username)
#set password $env(password)
set publish_dir $env(publish_dir)

spawn scp -r $publish_dir $username@$host:$publish_dir
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