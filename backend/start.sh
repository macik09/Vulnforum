#!/bin/bash
docker-compose down --volumes --remove-orphans
docker-compose build --no-cache
docker-compose up
