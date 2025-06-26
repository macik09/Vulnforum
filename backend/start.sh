#!/bin/bash
adb reverse tcp:5000 tcp:5000
docker-compose down --volumes --remove-orphans
docker-compose build --no-cache
docker-compose up
