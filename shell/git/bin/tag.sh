#!/bin/expect
set timeout 10
set username $env(username)
set password $env(password)
set tag [lindex $argv 0]
set msg [lindex $argv 1]
spawn git tag -a $tag -f -m $msg
spawn git push origin $tag
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
