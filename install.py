#!usr/bin/env python3

import os
import sys
import shutil
import requests

from zipfile import ZipFile


def install_path():
    os = platform.system()
    if os == 'Darwin':
        return os.environ['HOME'] + "/Library/Application Support/net.bc100dev/osintgram4j"
    elif os == 'Linux':
        if os.geteuid() != 0:
            return os.environ['HOME'] + "/.local/share/net.bc100dev/osintgram4j"
        else:
            return "/usr/share/bc100dev/osintgram4j"
    elif os == 'Windows':
        return os.environ['APPDATA'] + "/BC100Dev/Osintgram4j"

    return "__unsupported__"


def uninstall(dir):
    shutil.rmtree(dir)


def main():
    INSTALL_DIR = install_path()
    if os.path.exists(INSTALL_DIR):
        print("")
