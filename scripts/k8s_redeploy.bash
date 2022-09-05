#!/bin/bash
set -e
echo "=============Redeploy K8s========="

# This script should be run on your server and is expected that the k8s directory
# with the K8s descriptors is uploaded in ~/k8s-micro directory
k8s_dir=~/k8s-micro

kubectl delete all --all -n microservices
kubectl delete pvc --all -n microservices
kubectl delete pv --all -n microservices
kubectl delete ingress --all -n microservices
#kubectl delete namespace microservices

# kubectl delete all --all -n ingress-nginx
# kubectl delete namespace ingress-nginx

pushd $k8s_dir/k8s

kubectl apply -f ingress-controller/
kubectl apply -f 01_namespace.yaml
cat author-service-deployment.yaml | sed "s/{{AUTHOR_DOCKER_IMAGE}}/DOCKER_REGISTRY_ADDRESS\/author-service-docker/g" | kubectl apply -f -
cat book-service-deployment.yaml | sed "s/{{BOOK_DOCKER_IMAGE}}/DOCKER_REGISTRY_ADDRESS\/book-service-docker/g" | kubectl apply -f -
cat ui-service-deployment.yaml | sed "s/{{UI_DOCKER_IMAGE}}/DOCKER_REGISTRY_ADDRESS\/ui-service-docker/g" | kubectl apply -f -
cat microservices-ingress.yaml | sed "s/{{HOSTNAME}}/YOUR_SERVER_HOSTNAME/g" | kubectl apply -f -

popd


echo "Done"