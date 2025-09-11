#!/bin/bash

echo "Building Server"
cd iara-server/
rm -rf build
./gradlew clean build -x test
docker rmi -f iara-server:latest
docker build --no-cache -t iara-server:latest .

echo "Building UI"
cd ../iara-ui
rm -rf dist
npm install
npm run build:noEmit
docker rmi -f iara-iu:latest
docker build --no-cache -t iara-iu:latest .

echo "Build finished".
cd ..
docker compose up
