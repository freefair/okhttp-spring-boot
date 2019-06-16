#!/bin/bash

./gradlew publishToNexus -s -Psigning.keyId=$SIGNING_KEY_ID -Psigning.password=$SIGNING_PASSWORD -Psigning.secretKeyRingFile=$TRAVIS_BUILD_DIR/secring.gpg

./gradlew aggregateJavadocJar -s

curl --http1.1 --user 'user:$DOCS_PASS' --upload-file "build/libs/okhttp-spring-boot-$TRAVIS_TAG-javadoc.jar" "https://docs.freefair.io/$TRAVIS_TAG?path=okhttp-spring-boot"

./gradlew closeAndReleaseRepository -s
