#!/bin/expect
set timeout 30
set server $env(server)
set username $env(username)
set password $env(password)
spawn git pull
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
