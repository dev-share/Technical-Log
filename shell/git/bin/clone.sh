#!/bin/expect
set timeout 30
set server $env(server)
set username $env(username)
set password $env(password)
set branch [lindex $argv 0]
set url [lindex $argv 1]
set alias [lindex $argv 2]
spawn git clone -b $branch $url $alias
expect {
 "Username for 'http://$server':"
  {
    send "$username\n"
    expect "Password for 'http://$username@$server':" { send "$password\n"}
  }
 "Password for 'http://$username@$server':"
  {
    send "$password\n"
  }
}
expect eof
