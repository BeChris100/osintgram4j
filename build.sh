#!/bin/bash

# shellcheck disable=SC2074
# shellcheck disable=SC2162
# shellcheck disable=SC2016
# shellcheck disable=SC2164

source app_ver

if [ -f ".build-info" ]; then
    source .build-info

    if [ -z "$JAR_CMD" ] || [ -z "$JAVA_CMD" ] || [ -z "$JAVAC_CMD" ] || [ -z "$JLINK_CMD" ] || [ -z "$JDEPS_CMD" ] || [ -z "$JPACKAGE_CMD" ]; then
        echo "Error: Some JDK tools are missing. Please re-run the setup.sh to set up the required tools."
        exit 1
    fi

    if [ -f "$JAVA_CMD" ] && [ -x "$JAVA_CMD" ] &&
        [ -f "$JAVAC_CMD" ] && [ -x "$JAVAC_CMD" ] &&
        [ -f "$JLINK_CMD" ] && [ -x "$JLINK_CMD" ] &&
        [ -f "$JDEPS_CMD" ] && [ -x "$JDEPS_CMD" ] &&
        [ -f "$JPACKAGE_CMD" ] && [ -x "$JPACKAGE_CMD" ]; then
        JAVA_VERSION=$($JAVA_CMD --version 2>&1 | grep -oP 'openjdk \K\d+' | cut -d. -f1)

        if [ "$JAVA_VERSION" -ge 21 ]; then
            echo "Found JDK with version $JAVA_VERSION"
        elif [ "$JAVA_VERSION" -le 20 ]; then
            echo "You need at least the JDK version of 21. Reported Java Version is $JAVA_VERSION"
            echo "To obtain the newest JDK Version, run the setup.sh with the '--force-download' argument."
            exit 1
        else
            echo "The JDK Version could not be identified, and has returned a value of $JAVA_VERSION."
            exit 1
        fi
    else
        echo "Error: Some of more JDK Tools are missing from the JDK Installation."
        echo "Please verify the JDK instance at \"$JAVA_DEFAULT_HOME\" and reinstall, if necessary."
        echo "Alternatively, run the setup.sh with '--force-download' to obtain the latest JDK version compatible with the project."
        exit 1
    fi
else
    echo "No JDK has been initialized."
    echo "To set up and initialize a JDK instance, run the setup.sh script."
    exit 1
fi

mkdir -p build/pkg build/project/input build/project/commons build/project/instagram-api build/project/core

echo "## Compiling CXX code"
if [ "$OS_TYPE" == "osx" ]; then
    echo "* CXX code unsupported on macOS"
else
    CURRENT_WORKDIR=$(pwd)
    cd "cxx"
    mkdir -p build
    cd "build"
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_COMPILER=/usr/bin/x86_64-linux-gnu-gcc -DCMAKE_CXX_COMPILER=/usr/bin/x86_64-linux-gnu-gcc -DCMAKE_CPP_COMPILER=/usr/bin/x86_64-linux-gnu-g++ ..
    make
    cp libosintgram4j-cxx.so "$CURRENT_WORKDIR/build/project/input"

    cd "$CURRENT_WORKDIR"
fi

echo "## Compiling the Commons Library"
find commons/src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -d build/project/commons

echo "## Compiling the Instagram API"
find instagram_api/src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -cp build/project/commons:build/libs/json.jar -d build/project/instagram-api

echo "## Compiling the Core Application"
find src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -cp build/project/commons:build/project/instagram-api:build/libs/json.jar -d build/project/core

echo "## Adding resources to the Core Application"
cp -r src/net/bc100dev/osintgram4j/res build/project/core/net/bc100dev/osintgram4j/
cp src/net/bc100dev/osintgram4j/app_ver.cfg build/project/core/net/bc100dev/osintgram4j/app_ver.cfg

echo '## Making "commons.jar"'
"$JAR_CMD" -cf build/project/input/commons.jar -C build/project/commons .

echo '## Making "instagram-api.jar"'
"$JAR_CMD" -cf build/project/input/instagram-api.jar -C build/project/instagram-api .

echo '## Making "core.jar"'
"$JAR_CMD" -cfm build/project/input/core.jar META-INF/MANIFEST.MF -C build/project/core .

echo '## Obtaining the Application Java Modules'
JAVA_MODS="$($JDEPS_CMD --print-module-deps build/project/input/core.jar build/project/input/instagram-api.jar build/project/input/commons.jar build/libs/json.jar)"

# This adds the Certificates for the HTTPS Requests
JAVA_MODS="jdk.crypto.cryptoki,jdk.crypto.ec,$JAVA_MODS"
echo "Modules: $JAVA_MODS"

echo '## Building the Java Runtime'
if [ -d "build/runtime" ]; then
    echo "Cleaning up previous Runtime Image"
    rm -rf build/runtime
fi

"$JLINK_CMD" --module-path "$JAVA_DEFAULT_HOME/jmods" --output build/runtime --add-modules "$JAVA_MODS" --verbose

echo '## Building the Application Package'
cp build/libs/json.jar build/project/input/json.jar
"$JPACKAGE_CMD" -t app-image -n "$BUILD_NAME" --app-version "$BUILD_VERSION-$BUILD_VERSION_CODE" --runtime-image build/runtime -i build/project/input --main-jar core.jar --main-class net.bc100dev.osintgram4j.MainClass -d build/pkg --icon "extres/icon.png" --verbose

read -p "Do you want to install Osintgram (requires sudo privileges)? (Y/N): " INSTALL_CHOICE
if [[ "$INSTALL_CHOICE" =~ ^[Yy]$ ]]; then
    PREFIX=""

    if [ "$EUID" -ne 0 ]; then
        PREFIX="sudo"
    fi

    "$PREFIX" mkdir -p /usr/share/osintgram4j/
    "$PREFIX" cp -r build/pkg/osintgram4j/* /usr/share/osintgram4j
    "$PREFIX" ln -s /usr/share/osintgram4j/bin/osintgram4j /usr/bin/osintgram4j

    echo "## Installation complete"
    echo "To run Osintgram, with a Terminal open, run the 'osintgram4j' command."
    echo
    echo "In order to remove Osintgram4j from your system, delete the /usr/share/osintgram4j directory,"
    echo 'and run "rm -rf $(which osintgram4j)" with root privileges.'
else
    echo "You can run Osintgram from this directory and forwards by going to $PWD and run './build/pkg/osintgram4j/bin/osintgram4j'"
fi