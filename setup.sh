#!/bin/bash

# shellcheck disable=SC2129
# shellcheck disable=SC2162

set -e

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
    if [ -d "out" ]; then
        echo "A previous build was found."
        read -p "Do you want to continue? (Y/N): " SCRIPT_CHOICE
        if [[ "$SCRIPT_CHOICE" =~ ^[Nn]$ ]]; then
            exit 0
        fi

        echo "Rebuilding..."
        rm -rf out .build-info
    fi
fi

echo "// Operating System State"
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
    set +e

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

    set -e
}

function presence_java_tools() {
    set +e

    local java_cmd
    local javac_cmd
    local jpackage_cmd

    java_cmd=$(command -v "java")
    jar_cmd=$(command -v "jar")
    javac_cmd=$(command -v "javac")
    jpackage_cmd=$(command -v "jpackage")

    set -e

    if [ -n "$java_cmd" ] && [ -n "$jar_cmd" ] && [ -n "$javac_cmd" ] && [ -n "$jpackage_cmd" ]; then
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
        echo "JPACKAGE_CMD=$jpackage_cmd" >>.build-info
        echo "JAVA_DEFAULT_HOME=$(dirname "$(dirname "$(command -v java)")")" >>.build-info

        return 0
    else
        return 1
    fi
}

function get_jdk() {
    mkdir -p out/jdk/tmp

    RUN_DOWNLOAD="false"
    if [ "$FORCE_DOWNLOAD" == "true" ]; then
        RUN_DOWNLOAD="true"
    else
        if [ ! -f "out/jdk/tmp/jdk21.tar.gz" ]; then
            RUN_DOWNLOAD="true"
        fi
    fi

    if [ "$RUN_DOWNLOAD" == "true" ]; then
        if [[ "$IS_LINUX" == "true" ]]; then
            wget "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz" \
                -O out/jdk/tmp/jdk21.tar.gz
        elif [[ "$IS_OSX" == "true" ]]; then
            wget "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_macos-x64_bin.tar.gz" \
                -O out/jdk/tmp/jdk21.tar.gz
        fi

        TAR_CMD="$(command -v tar)"
        if [[ "$TAR_CMD" != "" ]]; then
            "$TAR_CMD" -xvzf out/jdk/tmp/jdk21.tar.gz --directory out/jdk
        fi

        mv out/jdk/jdk-21.0.2/* out/jdk
        rm -rf out/jdk/tmp out/jdk/jdk-21.0.2

        chmod +x out/jdk/bin/*

        echo "JAVA_CMD=$PWD/out/jdk/bin/java" >>.build-info
        echo "JAR_CMD=$PWD/out/jdk/bin/jar" >>.build-info
        echo "JAVAC_CMD=$PWD/out/jdk/bin/javac" >>.build-info
        echo "JLINK_CMD=$PWD/out/jdk/bin/jlink" >>.build-info
        echo "JDEPS_CMD=$PWD/out/jdk/bin/jdeps" >>.build-info
        echo "JPACKAGE_CMD=$PWD/out/jdk/bin/jpackage" >>.build-info
        echo "JAVA_DEFAULT_HOME=$PWD/out/jdk" >>.build-info
    fi
}

function get_libs() {
    mkdir -p out/libs

    RUN_DOWNLOAD="false"

    if [ "$FORCE_DOWNLOAD" == "true" ]; then
        RUN_DOWNLOAD="true"
    else
        if [ ! -f "out/libs/json.jar" ]; then
            RUN_DOWNLOAD="true"
        fi
    fi

    if [ "$RUN_DOWNLOAD" == "true" ]; then
        # update version to 20240303
        wget "https://repo1.maven.org/maven2/org/json/json/20240303/json-20240303.jar" -O out/libs/json.jar
    fi
}

if [ "$#" -ge 1 ]; then
    if [ "$1" == "--force-download" ]; then
        FORCE_DOWNLOAD="true"
    fi

    if [ "$1" == "--clean-build" ] || [ "$1" == "--clean" ]; then
        echo "Cleaning up..."

        if [ -f ".build-info" ]; then
            rm -f .build-info
        fi

        if [ -d "build" ]; then
            rm -rf out-out
        fi
    fi
fi

verify_tools

if [ "$FORCE_DOWNLOAD" == "true" ]; then
    get_jdk
    get_libs
else
    if ! presence_java_tools; then
        get_jdk
    fi

    get_libs
fi

echo ">> Setup Process completed"
echo "To build the project, run the Build script to complete the building process."