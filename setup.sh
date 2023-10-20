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
echo "Is OSX (macOS): $IS_OSX"
echo ""
echo "Preparing files (JDK, Libraries)"
echo ""

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

        echo "JAVA_CMD=$java_cmd" >.java-tools
        echo "JAR_CMD=$jar_cmd" >>.java-tools
        echo "JAVAC_CMD=$javac_cmd" >>.java-tools
        echo "JLINK_CMD=$jlink_cmd" >>.java-tools
        echo "JDEPS_CMD=$jdeps_cmd" >>.java-tools
        echo "JPACKAGE_CMD=$jpackage_cmd" >>.java-tools
        echo "JAVA_DEFAULT_HOME=$(dirname "$(dirname "$(command -v java)")")" >>.java-tools

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
            wget "https://download.java.net/java/GA/jdk21/fd2272bbf8e04c3dbaee13770090416c/35/GPL/openjdk-21_linux-x64_bin.tar.gz" \
                -O build/jdk/tmp/jdk21.tar.gz
        elif [[ "$IS_OSX" == "true" ]]; then
            wget "https://download.java.net/java/GA/jdk21/fd2272bbf8e04c3dbaee13770090416c/35/GPL/openjdk-21_macos-x64_bin.tar.gz" \
                -O build/jdk/tmp/jdk21.tar.gz
        fi

        TAR_CMD="$(command -v tar)"
        if [[ "$TAR_CMD" != "" ]]; then
            "$TAR_CMD" -xvzf build/jdk/tmp/jdk21.tar.gz --directory build/jdk
        fi

        mv build/jdk/jdk-21/* build/jdk
        rm -rf build/jdk/tmp build/jdk/jdk-21

        chmod +x build/jdk/bin/*

        echo "JAVA_CMD=$PWD/build/jdk/bin/java" >.java-tools
        echo "JAR_CMD=$PWD/build/jdk/bin/jar" >>.java-tools
        echo "JAVAC_CMD=$PWD/build/jdk/bin/javac" >>.java-tools
        echo "JLINK_CMD=$PWD/build/jdk/bin/jlink" >>.java-tools
        echo "JDEPS_CMD=$PWD/build/jdk/bin/jdeps" >>.java-tools
        echo "JPACKAGE_CMD=$PWD/build/jdk/bin/jpackage" >>.java-tools
        echo "JAVA_DEFAULT_HOME=$PWD/build/jdk" >>.java-tools
    fi
}

function get_libs() {
    mkdir -p build/libs

    RUN_DOWNLOAD="false"

    if [ "$FORCE_DOWNLOAD" == "true" ]; then
        RUN_DOWNLOAD="true"
    else
        if [ ! -f "build/libs/json.jar" ] || [ ! -f "build/libs/commons-codec.jar" ]; then
            RUN_DOWNLOAD="true"
        fi
    fi

    if [ "$RUN_DOWNLOAD" == "true" ]; then
        wget "https://repo1.maven.org/maven2/org/json/json/20230618/json-20230618.jar" -O build/libs/json.jar
        wget https://repo1.maven.org/maven2/commons-codec/commons-codec/1.16.0/commons-codec-1.16.0.jar -O build/libs/commons-codec.jar
    fi
}

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
