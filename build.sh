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

PREFIX=""
if [ "$EUID" -ne 0 ]; then
    if [ -n "$(command -v sudo)" ]; then
        PREFIX="sudo"
    elif [ -n "$(command -v doas)" ]; then
        PREFIX="doas"
        echo "Consider using 'sudo' for operations, since it will spare you time with entering your password."
    else
        echo "Could not determine the Root Prefix (sudo / doas). Install the specific package first."
        exit 1
    fi
fi

if [ "$#" -ne 0 ]; then
    if [ "$1" == "--uninstall" ]; then
        echo "Uninstalling osintgram4j"
        "$PREFIX" rm -rf /usr/bin/osintgram4j

        if [ -d "/usr/share/osintgram4j" ]; then
            "$PREFIX" rm -rf /usr/share/osintgram4j
        elif [ -d "/usr/share/bc100dev/osintgram4j" ]; then
            "$PREFIX" rm -rf /usr/share/bc100dev/osintgram4j
        fi
    fi

    exit 0
fi

mkdir -p out/pkg out/project/input out/project/commons out/project/instagram-api out/project/core

echo "## Compiling CXX code"
if [ "$OS_TYPE" == "osx" ]; then
    echo "* CXX code unsupported on macOS"
else
    CURRENT_WORKDIR=$(pwd)
    cd "cxx"
    mkdir -p out
    cd "out"
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_COMPILER=/usr/bin/x86_64-linux-gnu-gcc -DCMAKE_CXX_COMPILER=/usr/bin/x86_64-linux-gnu-g++ ..
    make
    cp libosintgram4j-cxx.so "$CURRENT_WORKDIR/out/project/input"

    MINGW_C="$(command -v x86_64-w64-mingw32-gcc)"
    MINGW_CPP="$(command -v x86_64-w64-mingw32-g++)"

    if [ -n "$MINGW_C" ] && [ -n "$MINGW_CPP" ]; then
        echo "* Compiling CXX code for Windows"

        cd "$CURRENT_WORKDIR/cxx"
        mkdir win
        cd win
        cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_COMPILER="$MINGW_C" -DCMAKE_CXX_COMPILER="$MINGW_CPP" ..
        make
        cp osintgram4j-cxx.dll "$CURRENT_WORKDIR/out/project/input"
    fi

    cd "$CURRENT_WORKDIR"
fi

echo "## Compiling the Commons Library"
find commons/src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -d out/project/commons

echo "## Compiling the Instagram API"
find instagram_api/src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -cp out/project/commons:out/libs/json.jar -d out/project/instagram-api

echo "## Compiling the Osintgram4j API"
find modapi/src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -cp out/project/commons:out/libs/json.jar -d out/project/modapi

echo "## Compiling the Core Application"
find src -name "*.java" -type f -print0 | xargs -0 "$JAVAC_CMD" -cp out/project/modapi:out/project/commons:out/project/instagram-api:out/libs/json.jar -d out/project/core

echo "## Adding resources to the Core Application"
cp -r src/net/bc100dev/osintgram4j/res out/project/core/net/bc100dev/osintgram4j/

echo '## Making "commons.jar"'
"$JAR_CMD" -cf out/project/input/commons.jar -C out/project/commons .

echo '## Making "instagram-api.jar"'
"$JAR_CMD" -cf out/project/input/instagram-api.jar -C out/project/instagram-api .

echo '## Making "modapi.jar"'
"$JAR_CMD" -cf out/project/input/modapi.jar -C out/project/modapi .

echo '## Making "core.jar"'
"$JAR_CMD" -cfm out/project/input/core.jar META-INF/MANIFEST.MF -C out/project/core .

## Minimal Java Runtime Removal: Modding will be limited, if not removed

echo '## Building the Application Package'
cp out/libs/json.jar out/project/input/json.jar
cp AppSettings.cfg out/project/input/AppSettings.cfg

if [ -d "out/pkg/osintgram4j" ]; then
    rm -rf out/pkg/osintgram4j
fi

"$JPACKAGE_CMD" -t app-image -n "$BUILD_NAME" --app-version "$BUILD_VERSION-$BUILD_VERSION_CODE" \
 -i out/project/input --main-jar core.jar --main-class net.bc100dev.osintgram4j.MainClass -d out/pkg --icon "extres/icon.png" \
 --java-options "-Xmx256m" --java-options "-Xms256m" --java-options '-Dog4j.location.app_dir=$APPDIR' \
 --java-options '-Dog4j.location.bin_dir=$BINDIR' --java-options '-Dog4j.location.root_dir=$ROOTDIR' --verbose

read -p "Do you want to install Osintgram (requires sudo privileges)? (Y/N): " INSTALL_CHOICE
if [[ "$INSTALL_CHOICE" =~ ^[Yy]$ ]]; then
    if [ -f "/usr/bin/osintgram4j" ]; then
        echo "Deleting previous installation"
        "$PREFIX" rm /usr/bin/osintgram4j

        if [ -d "/usr/share/osintgram4j" ]; then
            echo "Deleting old directory (moving to /usr/share/bc100dev/osintgram4j)"
            "$PREFIX" rm -rf /usr/share/osintgram4j
        else
            "$PREFIX" rm -rf /usr/share/bc100dev/osintgram4j
        fi
    fi

    echo "Copying built files"
    "$PREFIX" mkdir -p /usr/share/bc100dev/osintgram4j/
    "$PREFIX" cp -r out/pkg/osintgram4j/* /usr/share/bc100dev/osintgram4j
    "$PREFIX" ln -s /usr/share/bc100dev/osintgram4j/bin/osintgram4j /usr/bin/osintgram4j

    read -p "Do you wish to create an Application Launcher (start from the Start Menu)? (Y/N): " LAUNCHER_CHOICE
    if [[ "$LAUNCHER_CHOICE" =~ ^[Yy]$ ]]; then
        "$PREFIX" cp extres/app_launcher.desktop /usr/share/applications/bc100dev-osintgram4j.desktop
    fi

    echo "## Installation complete"
    echo "To run Osintgram, with a Terminal open, run the 'osintgram4j' command."
    echo
    echo "In order to remove Osintgram4j from your system, delete the /usr/share/bc100dev/osintgram4j directory,"
    echo "and run 'rm -rf \$(which osintgram4j)' with root privileges. On old installations, the installation path was /usr/share/osintgram4j"
    echo
    echo "Otherwise, you can also re-run this building script with the argument '--uninstall' to have"
    echo "Osintgram4j automatically uninstalled."
else
    echo "You can run Osintgram from this directory and forwards by going to $PWD and run './out/pkg/osintgram4j/bin/osintgram4j'"
fi