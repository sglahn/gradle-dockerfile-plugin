#!/bin/bash

#./gradlew --include-build ../ dockerBuild --stacktrace
#./gradlew --include-build ../ tasks --stacktrace
./gradlew --include-build ../ dockerPush --info

