#ifdef __linux__
#include <unistd.h>
#endif

#ifdef _WIN32
#include <windows.h>
#endif

#include "UserIO.h"
#include "ExceptionHandle.h"


JNIEXPORT jint JNICALL Java_net_bc100dev_commons_utils_io_UserIO_getGid(JNIEnv* env, jclass javaClass) {
    int gid = -1;

#ifdef __linux__
    gid = getgid();
#endif

    if (gid == -1)
        throwJavaAppException(env, "GetGID method outside of Linux environments not allowed");

    return gid;
}

JNIEXPORT jint JNICALL Java_net_bc100dev_commons_utils_io_UserIO_getUid(JNIEnv* env, jclass javaClass) {
    int uid = -1;

#ifdef __linux__
    uid = getuid();
#endif

    if (uid == -1)
        throwJavaAppException(env, "GetUID method outside of Linux environments not allowed");

    return uid;
}

JNIEXPORT jboolean JNICALL Java_net_bc100dev_commons_utils_io_UserIO_nIsAdmin(JNIEnv *, jclass) {
    bool admin;

#ifdef _WIN32
    HANDLE hToken = nullptr;
    TOKEN_ELEVATION elevation;
    DWORD dwSize;

    if (!OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY, &hToken))
        return admin;

    if (!GetTokenInformation(hToken, TokenElevation, &elevation, sizeof(elevation), &dwSize))
        return admin;

    if (elevation.TokenIsElevated)
        admin = true;

    if (hToken != nullptr) {
        CloseHandle(hToken);
        hToken = nullptr;
    }
#endif

#ifdef __linux__
    admin = getuid() == 0 && getgid() == 0;
#endif

    return admin;
}