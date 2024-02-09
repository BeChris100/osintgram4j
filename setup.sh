#!/bin/bash

# shellcheck disable=SC2129
# shellcheck disable=SC2162

IS_LINUX="false"
IS_OSX="false"

FORCE_DOWNLOAD="false"

if [[ "$(uname)" == "Linux" ]]; then
    IS_LINUX="true"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    IS_OSX="true"
fi

if [[ "$IS_LINUX" == "false" ]] && [[ "$IS_OSX" == "false" ]]; then
    echo "The operating system could not be checked correctly."
    exit 1
fi

if [ -f ".build-info" ]; then
    if [ -d "build" ]; then
        echo "A previous build was found."
        read -p "Do you want to continue? (Y/N): " SCRIPT_CHOICE
        if [[ "$SCRIPT_CHOICE" =~ ^[Nn]$ ]]; then
            exit 0
        fi

        echo "Rebuilding..."
        rm -rf build .build-info
    fi
fi

echo "## Operating System State"
echo "Is Linux: $IS_LINUX"
echo "Is OS X (macOS): $IS_OSX"
echo ""
echo "Preparing files (JDK, Libraries)"
echo ""

if [ "$IS_LINUX" == "true" ]; then
    echo "OS_TYPE=linux" > .build-info
elif [ "$IS_OSX" == "true" ]; then
    echo "OS_TYPE=osx" > .build-info
fi

REQ_PKG="tar wget cmake make gcc"

function verify_tools() {
    # GCC/clang Verification
    GCC_CMD=$(command -v "x86_64-linux-gnu-gcc")
    GPP_CMD=$(command -v "x86_64-linux-gnu-g++")
    CMAKE_CMD=$(command -v "cmake")
    MAKE_CMD=$(command -v "make")

    # shellcheck disable=SC2236
    if [ -z "$CMAKE_CMD" ]; then
        echo "cmake command not found."
        echo "To proceed with the build, install the following packages:"
        echo "$REQ_PKG"
        exit 1
    fi

    if [ -z "$MAKE_CMD" ]; then
        echo "make command not found (required for cmake)"
        echo "To proceed with the build, install the following packages:"
        echo "$REQ_PKG"
        exit 1
    fi

    echo "CMAKE_CMD=$CMAKE_CMD" >> .build-info

    if [ -z "$GCC_CMD" ]; then
        echo "No C/C++ compiler present. Requires x86_64-linux-gnu-gcc present."
        exit 1
    fi

    if [ -z "$GPP_CMD" ]; then
        echo "No C/C++ compiler present. Requires x86_64-linux-gnu-g++ present."
        exit 1
    fi

    TAR_CMD=$(command -v "tar")
    WGET_CMD=$(command -v "wget")

    if [ -z "$TAR_CMD" ]; then
        echo "tar command not found."
        echo "Required for extracting necessary files."
        echo "To proceed with the build, install the following packages:"
        echo "$REQ_PKG"
        exit 1
    fi

    if [ -z "$WGET_CMD" ]; then
        echo "wget command not found."
        echo "Required for downloading necessary files."
        echo "To proceed with the build, install the following packages:"
        echo "$REQ_PKG"
        exit 1
    fi
}

function presence_java_tools() {
    local java_cmd
    local javac_cmd
    local jlink_cmd
    local jdeps_cmd
    local jpackage_cmd

    java_cmd=$(command -v "java")
    jar_cmd=$(command -v "jar")
    javac_cmd=$(command -v "javac")
    jlink_cmd=$(command -v "jlink")
    jdeps_cmd=$(command -v "jdeps")
    jpackage_cmd=$(command -v "jpackage")

    if [ -n "$java_cmd" ] && [ -n "$jar_cmd" ] && [ -n "$javac_cmd" ] && [ -n "$jlink_cmd" ] && [ -n "$jdeps_cmd" ] && [ -n "$jpackage_cmd" ]; then
        echo "JDK Tools have been found."
        
        JAVA_VERSION=$($java_cmd --version 2>&1 | grep -oP 'openjdk \K\d+' | cut -d. -f1)
        if [ "$JAVA_VERSION" -le 20 ]; then
            echo "JDK version $JAVA_VERSION found, requires 21; require download"
            
            get_jdk
            return 0
        fi

        echo "JAVA_CMD=$java_cmd" >>.build-info
        echo "JAR_CMD=$jar_cmd" >>.build-info
        echo "JAVAC_CMD=$javac_cmd" >>.build-info
        echo "JLINK_CMD=$jlink_cmd" >>.build-info
        echo "JDEPS_CMD=$jdeps_cmd" >>.build-info
        echo "JPACKAGE_CMD=$jpackage_cmd" >>.build-info
        echo "JAVA_DEFAULT_HOME=$(dirname "$(dirname "$(command -v java)")")" >>.build-info

        return 0
    else
        return 1
    fi
}

function get_jdk() {
    mkdir -p build/jdk/tmp

    RUN_DOWNLOAD="false"
    if [ "$FORCE_DOWNLOAD" == "true" ]; then
        RUN_DOWNLOAD="true"
    else
        if [ ! -f "build/jdk/tmp/jdk21.tar.gz" ]; then
            RUN_DOWNLOAD="true"
        fi
    fi

    if [ "$RUN_DOWNLOAD" == "true" ]; then
        if [[ "$IS_LINUX" == "true" ]]; then
            wget "https://download.java.net/java/GA/jdk21.0.1/415e3f918a1f4062a0074a2794853d0d/12/GPL/openjdk-21.0.1_linux-x64_bin.tar.gz" \
                -O build/jdk/tmp/jdk21.tar.gz
        elif [[ "$IS_OSX" == "true" ]]; then
            wget "https://download.java.net/java/GA/jdk21.0.1/415e3f918a1f4062a0074a2794853d0d/12/GPL/openjdk-21.0.1_macos-x64_bin.tar.gz" \
                -O build/jdk/tmp/jdk21.tar.gz
        fi

        TAR_CMD="$(command -v tar)"
        if [[ "$TAR_CMD" != "" ]]; then
            "$TAR_CMD" -xvzf build/jdk/tmp/jdk21.tar.gz --directory build/jdk
        fi

        mv build/jdk/jdk-21.0.1/* build/jdk
        rm -rf build/jdk/tmp build/jdk/jdk-21.0.1

        chmod +x build/jdk/bin/*

        echo "JAVA_CMD=$PWD/build/jdk/bin/java" >>.build-info
        echo "JAR_CMD=$PWD/build/jdk/bin/jar" >>.build-info
        echo "JAVAC_CMD=$PWD/build/jdk/bin/javac" >>.build-info
        echo "JLINK_CMD=$PWD/build/jdk/bin/jlink" >>.build-info
        echo "JDEPS_CMD=$PWD/build/jdk/bin/jdeps" >>.build-info
        echo "JPACKAGE_CMD=$PWD/build/jdk/bin/jpackage" >>.build-info
        echo "JAVA_DEFAULT_HOME=$PWD/build/jdk" >>.build-info
    fi
}

function get_libs() {
    mkdir -p build/libs

    RUN_DOWNLOAD="false"

    if [ "$FORCE_DOWNLOAD" == "true" ]; then
        RUN_DOWNLOAD="true"
    else
        if [ ! -f "build/libs/json.jar" ]; then
            RUN_DOWNLOAD="true"
        fi
    fi

    if [ "$RUN_DOWNLOAD" == "true" ]; then
        wget "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar" -O build/libs/json.jar
    fi
}

if [ -f "extres/ee.enc" ]; then
    # fuck you lookin at? git don't give me no hook availability (I assume)
    rm extres/ee.enc
fi

verify_tools

if [ "$#" -ge 1 ]; then
    if [ "$1" == "--force-download" ]; then
        FORCE_DOWNLOAD="true"
    fi
fi

if [ "$FORCE_DOWNLOAD" == "true" ]; then
    get_jdk
    get_libs
else
    if ! presence_java_tools; then
        get_jdk
    fi

    get_libs
fi

echo "## Setup Process completed"
echo "To continue, run the Build Script to complete the build process."
