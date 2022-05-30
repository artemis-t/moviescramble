#!/bin/bash

set -e

docker stack rm moviescramble
echo
echo "Waiting for Docker resource cleanup..."
while [ "$(docker network ls | grep -c moviescramble_default)" == 1 ]; do
    sleep 1
done

echo "done"
echo
