#include <iostream>
#include <vector>

#ifdef _WIN32
#include <windows.h>
#else
#include <fstream>
#include <sstream>

#endif

#include <app/MemoryInfo.h>

std::vector<long> SYS_MEM_INFO() {
    std::vector<long> info(3, 0);

#ifdef _WIN32
    MEMORYSTATUSEX memoryInfo;
    memoryInfo.dwLength = sizeof(memoryInfo);
    GlobalMemoryStatusEx(&memoryInfo);

    info[0] = static_cast<long>(memoryInfo.ullTotalPhys);
    info[1] = static_cast<long>(memoryInfo.ullAvailPhys);
    info[2] = static_cast<long>(memoryInfo.ullTotalPhys - memoryInfo.ullAvailPhys);
#else
    std::ifstream memFile("/proc/meminfo");
    if (!memFile.is_open()) {
        std::cerr << "Unable to open /proc/meminfo" << std::endl;
        return info;
    }

    std::string line;
    long totalMemory = -1;
    long freeMemory = -1;
    long availableMemory = -1;

    while (std::getline(memFile, line)) {
        if (totalMemory != -1 && freeMemory != -1 && availableMemory != -1)
            break;

        std::istringstream iss(line);
        std::string key;
        long value;

        if (iss >> key >> value) {
            if (key == "MemTotal:")
                totalMemory = value * 1024;
            else if (key == "MemFree:")
                freeMemory = value * 1024;
            else if (key == "MemAvailable:")
                availableMemory = value * 1024;
        }
    }

    memFile.close();

    info[0] = totalMemory;
    info[1] = availableMemory;
    info[2] = freeMemory;
#endif

    return info;
}

JNIEXPORT jlong JNICALL Java_net_bc100dev_osintgram4j_cmd_AppRuntime_sysTotalMemory(JNIEnv* env, jclass clazz) {
    std::vector<long> info = SYS_MEM_INFO();
    return info[0];
}

JNIEXPORT jlong JNICALL Java_net_bc100dev_osintgram4j_cmd_AppRuntime_sysAvailableMemory(JNIEnv* env, jclass clazz) {
    std::vector<long> info = SYS_MEM_INFO();
    return info[1];
}

JNIEXPORT jlong JNICALL Java_net_bc100dev_osintgram4j_cmd_AppRuntime_sysFreeMemory(JNIEnv* env, jclass clazz) {
    std::vector<long> info = SYS_MEM_INFO();
    return info[2];
}
