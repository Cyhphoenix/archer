#!/bin/sh
cd ~

if [ -d archer ]; then
  rm -rf archer
fi

git clone https://github.com/Cyhphoenix/archer.git

WORK_DIR=~/archer
cd $WORK_DIR

mvn clean package -Dmaven.test.skip=true

if [[ "`ps aux | grep 'archer-web-0.0.1-SNAPSHOT.jar' | grep -v 'grep' | wc -l`" != "0" ]]; then
	kill -9 `ps aux |grep 'archer-web-0.0.1-SNAPSHOT.jar' | grep -v 'grep'|awk '{print $2}'`
fi

java -jar ./archer-web/target/archer-web-0.0.1-SNAPSHOT.jar > $WORK_DIR/stdout.log &