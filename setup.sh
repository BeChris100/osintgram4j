#!/bin/bash

# shellcheck disable=SC2129

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

echo "## Operating System State"
echo "Is Linux: $IS_LINUX"
echo "Is OS X (macOS): $IS_OSX"
echo ""
echo "Preparing files (JDK, Libraries)"
echo ""

if [ "$IS_LINUX" == "true" ]; then
    echo "OS_TYPE=linux"
elif [ "$IS_OSX" == "true" ]; then
    echo "OS_TYPE=osx"
fi

function verify_gcc() {
    # GCC/clang Verification
    CLANG_CMD=$(command -v "clang")
    GCC_CMD=$(command -v "gcc")
    CMAKE_CMD=$(command -v "cmake")
    MAKE_CMD=$(command -v "make")

    # shellcheck disable=SC2236
    if [ -z "$CMAKE_CMD" ]; then
        echo "cmake command not found."
        echo "To proceed with the build, you need to have GCC/clang installed, along with cmake and make."
        exit 1
    fi

    if [ -z "$MAKE_CMD" ]; then
        echo "make command not found (required for cmake)"
        echo "To proceed with the build, you need to have GCC/clang installed, along with cmake and make."
        exit 1
    fi

    echo "CMAKE_CMD=$CMAKE_CMD" >> .build-info

    if [ -n "$GCC_CMD" ]; then
        echo "CXX_CMD=gcc:$GCC_CMD" >> .build-info
    elif [ -n "$CLANG_CMD" ]; then
        echo "CXX_CMD=clang:$CLANG_CMD" >> .build-info
    else
        echo "No C/C++ compiler present."
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

        mv build/jdk/jdk-21/* build/jdk
        rm -rf build/jdk/tmp build/jdk/jdk-21

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

verify_gcc

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
