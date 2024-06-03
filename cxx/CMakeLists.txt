cmake_minimum_required(VERSION 3.10)
project(osintgram4j_natives)

set(CMAKE_CXX_STANDARD 17)

set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static-libgcc -static-libstdc++")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall")

include_directories("include")

if (${CMAKE_CXX_COMPILER} MATCHES "mingw" OR ${CMAKE_SYSTEM_NAME} MATCHES "win")
    include_directories("include/win32")
elseif (${CMAKE_CXX_COMPILER} MATCHES "linux" OR ${CMAKE_SYSTEM_NAME} MATCHES "nux")
    include_directories("include/linux")
endif ()

add_library(osintgram4j SHARED
        src/ConsoleSize.cpp
        src/UserIO.cpp
        src/ExceptionHandle.cpp
        src/MemoryInfo.cpp)

add_executable(og4j-editor
        src/ProjectFileEditor.cpp)

# Windows on Linux compilation
if (${CMAKE_CXX_COMPILER} MATCHES "mingw" OR ${CMAKE_SYSTEM_NAME} MATCHES "win")
    set_target_properties(osintgram4j PROPERTIES PREFIX "")
    set_target_properties(osintgram4j PROPERTIES SUFFIX ".dll")
endif ()