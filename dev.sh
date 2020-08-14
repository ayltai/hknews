#!/usr/bin/env sh

docker build --tag "${PWD##*/}:latest" .

docker run \
  --privileged \
  -v "$(pwd)":/"${PWD##*/}" \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /sys/fs/cgroup:/sys/fs/cgroup:ro \
  -it --rm \
  "${PWD##*/}:latest"
