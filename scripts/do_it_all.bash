#!/bin/bash
set -e
echo "1. Rebuild JARs"
echo "2. Rebuild Docker images"
echo "3. Push Docker images"
echo "4. Upload K8s files"
echo "5. Redeploy K8s"

DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Build Java projects

pushd "$DIR"/../
./mvnw clean install -Pprod -Dspring-boot.run.jvmArguments="-DserverAddress=https://YOUR_SERVER_ADDRESS:PORT_NUM -Dversion=APP_VERSION" "$@"
popd

# Build and push Docker images

"$DIR"/../book-service/scripts/build_docker_image.bash YOUR_DOCKER_REGISTRY:PORT_NUM/book-service-docker
"$DIR"/../author-service/scripts/build_docker_image.bash YOUR_DOCKER_REGISTRY:PORT_NUM/author-service-docker
"$DIR"/../ui-service/scripts/build_docker_image.bash YOUR_DOCKER_REGISTRY:PORT_NUM/ui-service-docker

# Upload K8s

serverIp=YOUR_SERVER_IP
pass=YOUR_SERVER_PASS

command="sshpass -p $pass ssh -t root@$serverIp rm -rf ~/k8s-micro;mkdir ~/k8s-micro"
echo "Run command: '$command'"
$command

command="sshpass -p $pass scp -r $DIR/../k8s/ root@$serverIp:~/k8s-micro"

echo "Run command: '$command'"
$command

command="sshpass -p $pass ssh -t root@$serverIp ~/scripts/k8s_redeploy.bash"

echo "Run command: '$command'"
$command

echo "Done"