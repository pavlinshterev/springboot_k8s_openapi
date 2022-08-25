#!/bin/bash

if [ -z "$1" ]; then
   echo "Docker image name is required. e.g 'debug_in_docker.bash YOUR_DOCKER_REGISTRY/docker-image-name'" && exit 1
fi

IMAGE=$1
echo "Docker image: $IMAGE"
docker run -e "JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" -p 8001:30001 -p 5005:5005 -t "$IMAGE"