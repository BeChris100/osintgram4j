/*
 * This file explicitly requires Root Access to being able to write content
 * that are usually restricted without Administrative rights. Currently only
 * compatible under Linux.
 */

#ifdef __linux__
#include <unistd.h>
#endif

#ifdef _WIN64
#include <windows.h>
#endif

#include <fstream>
#include <iostream>

bool HasPermissions() {
    bool perms = false;

#ifdef __linux__
    perms = geteuid() == 0;
#endif

#ifdef _WIN64
    HANDLE hToken = nullptr;
    TOKEN_ELEVATION elevation;
    DWORD dwSize;

    if (!OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY, &hToken))
        return perms;

    if (!GetTokenInformation(hToken, TokenElevation, &elevation, sizeof(elevation), &dwSize))
        return perms;

    if (elevation.TokenIsElevated)
        perms = true;

    if (hToken != nullptr) {
        CloseHandle(hToken);
        hToken = nullptr;
    }
#endif

    return perms;
}

int main(int argc, char** argv) {
    if (argc != 3) {
        std::cerr << "usage:" << std::endl;
        std::cerr << "og4j-editor [use_file] [to_file]" << std::endl << std::endl;
        std::cerr << "where:" << std::endl;
        std::cerr << "[use_file]    The file that is used to copy file" << std::endl;
        std::cerr << "[to_file]     The file that requires administrative/root rights to modify the file" << std::endl;
        return 1;
    }

    const char* sourceFilePath = argv[1];
    const char* destFilePath = argv[2];

    if (!HasPermissions()) {
#ifdef _WIN64
        std::cerr << "This program requires administrative permissions to able to modify application-specific files" << std::endl;
#elif __linux__
        std::cerr << "This process requires Root permissions to access application-specific files" << std::endl;
#endif
        return 1;
    }

    std::ifstream sourceFile(sourceFilePath, std::ios::in);
    if (!sourceFile.is_open()) {
        std::cerr << "Failed to open source file: " << sourceFilePath << std::endl;
        return 1;
    }

    std::ofstream destFile(destFilePath, std::ios::out | std::ios::trunc);
    if (!destFile.is_open()) {
        std::cerr << "Failed to open destination file: " << destFilePath << std::endl;
        sourceFile.close();
        return 1;
    }

    destFile << sourceFile.rdbuf();
    destFile << "\n";

    if (sourceFile.fail() || destFile.fail()) {
        std::cerr << "Error occurred during file copy" << std::endl;

        sourceFile.close();
        destFile.close();

        return 1;
    }

    std::cout << "File copy successful" << std::endl;

    sourceFile.close();
    destFile.close();

    return 0;
}