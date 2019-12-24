#!/bin/expect
set timeout 10
set server $env(server)
set username $env(username)
set password $env(password)
set msg [lindex $argv 0]
spawn git commit -a -m $msg
spawn git push origin
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
