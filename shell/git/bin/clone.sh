#!/bin/expect
set timeout 10 
set username $env(username)
set password $env(password)
set branch [lindex $argv 0]
set url [lindex $argv 1]
set alias [lindex $argv 2]
spawn git clone -b $branch $url $alias
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
