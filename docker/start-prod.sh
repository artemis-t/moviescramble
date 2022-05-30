#!/bin/bash

set -e

if [ -z "$NEO4J_USER" ]; then
  echo "!! Please provide NEO4J_USER envvar." >&2
  exit 1
fi

if [ -z "$NEO4J_PASS" ]; then
  echo "!! Please provide NEO4J_PASS envvar." >&2
  exit 1
fi

NEO4J_BOLT_PORT=7687
NEO4J_HTTP_PORT=7474

function waitForService() {
    local name=$1
    local hostname=$2
    local port=$3
    echo "Waiting for ${name} to become available..."
    set +m
    while ! timeout 2 telnet "$hostname" ${port} 2>&1 < <(echo) | grep 'Connection closed by foreign host' >/dev/null; do
        sleep 1
    done
    set -m
}

# navigate to residing directory of script
cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# create directories
if [ ! -e neo4j-prod-conf ]; then
    mkdir neo4j-prod-conf
fi
if [ ! -e neo4j-prod-logs ]; then
    mkdir neo4j-prod-logs
fi
if [ ! -e neo4j-prod-data ]; then
    mkdir neo4j-prod-data
fi

export NEO4J_UID=${UID}
export NEO4J_GID=${GROUPS[0]}

# Deploy docker stack
docker stack deploy -c stack-prod.yaml moviescramble

echo
waitForService neo4j 127.0.0.1 ${NEO4J_HTTP_PORT}

echo
echo "--"
echo
echo "Prod environment started successfully"
echo
echo "Services:"
echo "  - neo4j | 127.0.0.1:${NEO4J_BOLT_PORT}"
echo "      - credentials: neo4j/ms-root"
echo "      - web interface: http://127.0.0.1:${NEO4J_HTTP_PORT}/"
echo
