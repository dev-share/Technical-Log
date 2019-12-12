#!/bin/expect
set timeout 30
set username $env(username)
set password $env(password)
set tag [lindex $argv 0]
set slave [lindex $argv 1]
set master [lindex $argv 2]
spawn git merge $slave --ff -m $tag
spawn git push origin $master
expect {
 "Username for 'http://172.21.32.31':"
  {
    send "$username\n"
    expect "Password for 'http://$username@172.21.32.31':" { send "$password\n"}
  }
 "Password for 'http://$username@172.21.32.31':"
  {
    send "$password\n"
  }
}
expect eof
