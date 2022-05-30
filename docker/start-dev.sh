#!/bin/bash

set -e

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
rm -rf neo4j-logs 2>/dev/null || true
mkdir  neo4j-logs 2>/dev/null || true
mkdir  neo4j-conf 2>/dev/null || true
mkdir  neo4j-data 2>/dev/null || true

export NEO4J_UID=${UID}
export NEO4J_GID=${GROUPS[0]}

# Deploy docker stack
docker stack deploy -c stack-dev.yaml moviescramble

echo
waitForService neo4j 127.0.0.1 ${NEO4J_HTTP_PORT}

echo
echo "--"
echo
echo "Dev environment started successfully"
echo
echo "Services:"
echo "  - neo4j | 127.0.0.1:${NEO4J_BOLT_PORT}"
echo "      - credentials: neo4j/ms-root"
echo "      - web interface: http://127.0.0.1:${NEO4J_HTTP_PORT}/"
echo
