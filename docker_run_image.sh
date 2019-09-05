#!/bin/bash

docker run \
       -it \
       -p 8899:8899 \
       --rm \
       --name nanovaadin-undertow \
       nanovaadin/undertow:latest

