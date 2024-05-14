#ifdef __linux__
#include <unistd.h>
#endif

#ifdef _WIN32
#include <windows.h>
#endif

#include <app/UserIO.h>
#include <app/ExceptionHandle.h>


JNIEXPORT jint JNICALL Java_net_bc100dev_commons_utils_io_UserIO_getGid(JNIEnv* env, jclass javaClass) {
    unsigned int gid = -1;

#ifdef __linux__
    gid = getgid();
#endif

    if (gid == -1)
        throwJavaAppException(env, "GetGID method outside of Linux environments not allowed");

    return static_cast<int>(gid);
}

JNIEXPORT jint JNICALL Java_net_bc100dev_commons_utils_io_UserIO_getUid(JNIEnv* env, jclass javaClass) {
    unsigned int uid = -1;

#ifdef __linux__
    uid = getuid();
#endif

    if (uid == -1)
        throwJavaAppException(env, "GetUID method outside of Linux environments not allowed");

    return static_cast<int>(uid);
}

JNIEXPORT void JNICALL Java_net_bc100dev_commons_utils_io_UserIO_setUid(JNIEnv *env, jclass, jint value) {
#ifdef __linux__
    if (setuid(value))
        throwJavaAppException(env, &"SetUID failed for id " [ value]);
#else
    throwJavaAppException(env, "SetUID method outside of Linux environments not allowed");
#endif
}

JNIEXPORT void JNICALL Java_net_bc100dev_commons_utils_io_UserIO_setGid(JNIEnv *env, jclass, jint value) {
#ifdef __linux__
    if (setgid(value))
        throwJavaAppException(env, &"SetGID failed for id " [ value]);
#else
    throwJavaAppException(env, "SetGID method outside of Linux environments not allowed");
#endif
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