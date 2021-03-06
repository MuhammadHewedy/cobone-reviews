#!/bin/bash

#build
cd java-backend
mvn clean package -DskipTests=true
cd -

#stop
PID=$(ps -ef | grep java.*\.jar | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo "Application is already stopped"
else
    kill $PID
fi

#start
LOG_DIR=/var/app/log
#note before starting the app, the enviroment variables defined in README.md should be declared in the environment
nohup java -Djava.security.egd=file:/dev/./urandom -jar java-backend/target/*.jar | tee $LOG_DIR/cobone-reviews.out &
