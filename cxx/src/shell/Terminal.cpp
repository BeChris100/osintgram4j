#ifdef __linux__
#include <ncurses.h>
#include <cstring>
#include <vector>
#include <iostream>

void initShell() {
    initscr();
    cbreak();
    keypad(stdscr, TRUE);
}

void clearShell() {
    clear();
    refresh();
}

char* inputShell(char* ps1, std::vector<std::string> history) {
    int historyIndex = -1;
    int ch;
    static char inputStr[32768] = {0};
    int inputPos = 0, cursorX = 0;

    mvprintw(0, 0, "%s", ps1);
    move(1, 0);

    while ((ch = getch()) != '\n') {
        switch (ch) {
            case KEY_UP:
            case KEY_DOWN:
                // History navigation (simplified for this example)
                if (ch == KEY_UP && historyIndex < (int)history.size() - 1) historyIndex++;
                else if (ch == KEY_DOWN && historyIndex > -1) historyIndex--;

                if (historyIndex >= 0 && historyIndex < (int)history.size()) {
                    strncpy(inputStr, history[historyIndex].c_str(), sizeof(inputStr) - 1);
                    inputPos = cursorX = strlen(inputStr);
                } else {
                    historyIndex = -1;
                    memset(inputStr, 0, sizeof(inputStr));
                    inputPos = cursorX = 0;
                }
                break;
            case KEY_LEFT:
                if (inputPos > 0) {
                    inputPos--;
                    cursorX--;
                }
                break;
            case KEY_RIGHT:
                if (inputPos < (int)strlen(inputStr)) {
                    inputPos++;
                    cursorX++;
                }
                break;
            default:
                if (inputPos < sizeof(inputStr) - 1) {
                    memmove(&inputStr[inputPos + 1], &inputStr[inputPos], strlen(inputStr) - inputPos + 1);
                    inputStr[inputPos] = (char)ch;
                    inputPos++;
                    cursorX++;
                }
                break;
        }

        mvprintw(0, 0, "%s", ps1);
        mvprintw(1, 0, "%s", inputStr);
        move(1, cursorX);
    }

    return inputStr;
}

void closeShell() {
    endwin();
}
#endif