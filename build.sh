#!/bin/bash

echo "Building Server"
cd iara-server
./gradlew clean build -x test

echo "Building UI"
cd ../iara-ui
npm install
npm run build
cd ..
echo "Build finished".