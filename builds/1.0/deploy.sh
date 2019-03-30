./killRemote.sh
scp -r tc.jar pi@pi:~/tc/
scp *.properties pi@pi:~/tc/
scp *.sh pi@pi:~/tc/
./startRemote.sh
