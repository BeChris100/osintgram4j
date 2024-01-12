cmake_minimum_required(VERSION 3.10)
project(osintgram4j_natives)

set(CMAKE_CXX_STANDARD 17)

include_directories("include/java_natives")

if (${CMAKE_CXX_COMPILER} MATCHES "mingw" OR ${CMAKE_SYSTEM_NAME} MATCHES "win")
    include_directories("include/java_natives/win32")
elseif (${CMAKE_CXX_COMPILER} MATCHES "linux" OR ${CMAKE_SYSTEM_NAME} MATCHES "nux")
    include_directories("include/java_natives/linux")
endif ()

add_library(osintgram4j-cxx SHARED
        src/ConsoleSize.cpp src/ConsoleSize.h
        src/UserIO.cpp src/UserIO.h
        src/ExceptionHandle.cpp
        src/ExceptionHandle.h
        src/MemoryInfo.cpp
)

# Windows on Linux compilation
if (${CMAKE_CXX_COMPILER} MATCHES "mingw" OR ${CMAKE_SYSTEM_NAME} MATCHES "win")
    set_target_properties(osintgram4j-cxx PROPERTIES PREFIX "")
    set_target_properties(osintgram4j-cxx PROPERTIES SUFFIX ".dll")
endif ()