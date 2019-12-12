#!/bin/expect
set timeout 10
set username $env(username)
set password $env(password)
spawn git pull
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
