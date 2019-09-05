#!/bin/bash
docker build -t nanovaadin/undertow .
#docker tag nanovaadin/undertow:latest nanovaadin/undertow:20190826-001
#docker push nanovaadin/undertow:20190826-001
docker push nanovaadin/undertow:latest
