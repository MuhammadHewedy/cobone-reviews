#!/bin/bash

#build
cd java-backend
mvn package -DskipTests=true
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
#note before starting the app, the enviroment variables defined in README.md should be declared in the environment
nohup java -jar java-backend/target/*.jar  &
