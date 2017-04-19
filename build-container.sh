#!/bin/bash
gradle clean :pos-print:build :pos-print:shadowJar

STATUS=$?
if [ $STATUS == 1 ]; then
    echo "Build failed with errors."
    exit
fi

docker build -t clouway/posprint:1.0 .
docker push clouway/posprint:1.0
