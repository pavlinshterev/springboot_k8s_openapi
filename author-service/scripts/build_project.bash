#!/bin/bash
set -e
echo "=============Build Java project========="

if [ -z "$1" ] || [ -z "$2" ]; then
   echo "Server URL and project version is required. e.g 'build_project.bash https://YOUR_SERVER:PORT 1.0'" && exit 1
fi

DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

export JAVA_HOME=$(/usr/libexec/java_home -v 17)

SERVER_URL=$1
VERSION=$2
echo "Building project using server: $SERVER_URL and version: $VERSION"
pushd "$DIR"/../
./mvnw clean install -Pprod -Dspring-boot.run.jvmArguments="-DserverAddress=$SERVER_URL -Dversion=$VERSION"
popd
