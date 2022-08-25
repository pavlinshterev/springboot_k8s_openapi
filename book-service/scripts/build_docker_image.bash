#!/bin/bash
set -e
echo "=============Build and push the docker image========="

if [ -z "$1" ]; then
   echo "Docker image name is required. e.g 'build_docker_image.bash YOUR_DOCKER_REGISTRY/docker-image-name'" && exit 1
fi

DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
IMAGE=$1
echo "Building Docker image: $IMAGE"
docker build -t $IMAGE $DIR/../
docker push $IMAGE