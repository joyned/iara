#!/bin/bash

echo "Building Server"
cd iara-server
./gradlew clean build

echo "Building UI"
cd ../iara-ui
nvm use
npm run build
cd ..

echo "Build finished".

docker rm -f iara
docker build -t iara-app . --no-cache
docker run -p 9090:80 --name iara iara-app