#!/usr/bin/env bash

WORKSPACE="$(pwd)"

echo "Building integration bridge"
mvn clean install -P docker

echo "Building docker image"
echo "docker build -f docker/Dockerfile -t integration-bridge ."
docker build -f docker/Dockerfile -t integration-bridge .