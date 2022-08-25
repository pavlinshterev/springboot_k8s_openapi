#!/bin/bash
set -e
echo "============Initial configuration for K8s cluster============"

secret_name="tls-certs"
kubectl delete secret/$secret_name -n microservices || echo Deleting secret $secret_name failed. Probably secret does not exist.
echo Creating a secret $secret_name
kubectl create secret tls $secret_name --key /certs/privkey.pem --cert /certs/fullchain.pem -n microservices

secret_name="reg-creds"
kubectl delete secret/$secret_name -n microservices || echo Deleting secret $secret_name failed. Probably secret does not exist.
echo Creating a secret $secret_name
kubectl create secret docker-registry $secret_name --docker-server=DOCKER_REGISTRY_ADDRESS --docker-username=DOCKER_REGISTRY_USER --docker-password=DOCKER_REGISTRY_PASS -n microservices

secret_name="db-creds"
kubectl delete secret/$secret_name -n microservices || echo Deleting secret $secret_name failed. Probably secret does not exist.

TMPFILE=$(mktemp)
trap "rm -f $TMPFILE" EXIT

cat > "$TMPFILE" <<- EOM
author_db_conn=jdbc:mysql://DB_SERVER_ADDRESS/AUTHOR_DB_NAME?serverTimezone=UTC&characterEncoding=UTF-8&enabledTLSProtocols=TLSv1.2
author_db_user=AUTHOR_DB_USER
author_db_pass=AUTHOR_DB_PASS
book_db_conn=jdbc:mysql://DB_SERVER_ADDRESS/BOOK_DB_NAME?serverTimezone=UTC&characterEncoding=UTF-8&enabledTLSProtocols=TLSv1.2
book_db_user=BOOK_DB_USER
book_db_pass=BOOK_DB_PASS
EOM

echo Creating a secret $secret_name
kubectl create secret generic $secret_name --from-env-file="$TMPFILE" -n microservices