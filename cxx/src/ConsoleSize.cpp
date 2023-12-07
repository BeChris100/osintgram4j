#ifdef _WIN32
#include <windows.h>
#endif

#ifdef __linux__
#include <sys/ioctl.h>
#include <unistd.h>
#endif

#include "ConsoleSize.h"


JNIEXPORT jint JNICALL Java_net_bc100dev_commons_Terminal_windowLines(JNIEnv *env, jclass javaClass) {
    int rows;

#ifdef _WIN32
    CONSOLE_SCREEN_BUFFER_INFO csbi;
    GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE), &csbi);

    rows = csbi.srWindow.Bottom - csbi.srWindow.Top + 1;
#endif

#ifdef __linux__
    struct winsize w{};
    ioctl(STDOUT_FILENO, TIOCGWINSZ, &w);

    rows = w.ws_row;
#endif

    return rows;
}

JNIEXPORT jint JNICALL Java_net_bc100dev_commons_Terminal_windowColumns(JNIEnv *env, jclass javaClass) {
    int columns;

#ifdef _WIN32
    CONSOLE_SCREEN_BUFFER_INFO csbi;
    GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE), &csbi);

    columns = csbi.srWindow.Right - csbi.srWindow.Left + 1;
#endif

#ifdef __linux__
    struct winsize w{};
    ioctl(STDOUT_FILENO, TIOCGWINSZ, &w);

    columns = w.ws_col;
#endif

    return columns;
}