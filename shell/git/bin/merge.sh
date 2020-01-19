#!/bin/expect
set timeout 30
set server $env(server)
set username $env(username)
set password $env(password)
set tag [lindex $argv 0]
set slave [lindex $argv 1]
set master [lindex $argv 2]
spawn git merge $slave -v -m $tag
spawn git push origin $master
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
