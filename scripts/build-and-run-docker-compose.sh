#!/bin/bash

echo "Building Server"
cd iara-server/
./gradlew clean build -x test
docker build -t iara-server .

echo "Building UI"
cd ../iara-ui
npm install
npm run build:noEmit
docker build -t iara-iu .

echo "Build finished".
cd ..
docker compose up
