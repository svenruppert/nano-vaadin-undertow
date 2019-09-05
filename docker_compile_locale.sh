#!/usr/bin/env bash

docker run \
      --rm \
      --name compile \
      -v "$(pwd)":/usr/src/mymaven \
      -w /usr/src/mymaven \
      svenruppert/maven-3.6.1-adopt:1.8.212-04:latest \
      mvn clean package -Dmaven.test.skip=false -Dvaadin-install-nodejs

