#!/bin/sh
cd ~
WORK_DIR=~/archer

if [ -d archer ]; then
  rm -rf archer
fi

git clone https://github.com/Cyhphoenix/archer.git

cd $WORK_DIR

mvn clean package -Dmaven.test.skip=true

java -jar ./archer-web/target/archer-web-0.0.1-SNAPSHOT.jar > $WORK_DIR/stdout.log &