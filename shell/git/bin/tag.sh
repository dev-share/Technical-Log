#!/bin/expect
set timeout 30
set server $env(server)
set username $env(username)
set password $env(password)
set tag [lindex $argv 0]
set msg [lindex $argv 1]
spawn git tag -a $tag -f -m $msg
spawn git push origin $tag
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
