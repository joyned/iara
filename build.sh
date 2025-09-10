#!/bin/bash

echo "Building Server"
cd iara-server
./gradlew clean build

echo "Building UI"
cd ../iara-ui
npm install
npm run build
cd ..
echo "Build finished".