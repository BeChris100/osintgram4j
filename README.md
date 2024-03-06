# OSINTgram (OSINT for Instagram)
Remember on how good our old, beloved Osintgram project became famous by Datalux,
and nowadays, it barely even functions? Yeah, ever since the lack of updates and
Instagram API changes, the original project on the
[base repository](https://github.com/Datalux/Osintgram) barely works anymore.
What I decided to do is to rewrite the whole entire project, including the IG
Private APIs, in my favorite language of all time, Java.

Note that this is still a work in the progress, as the original code is still
being analyzed, so that I can convert them into the functional Java code. It is
not expected to be fully functional as of now, as I recently started working on
Osintgram for Java.

---

## Disclaimer (Rules)
The use of any software, Osintgram / Osintgram4j included, is intended for
legitimate and ethical use only. [Me](https://github.com/BeChris100),
[the developer](https://github.com/Datalux) and other contributors of the Osintgram
project do not condone or support any form of unethical activities, such as Illegal
/ Black-Hat Hacking, or having malicious intents in the first place. It is essential
to follow all relevant laws and Instagram's ToS (Terms of Service), while using
this software.

By using the Osintgram Project, you acknowledge and agree that:

- You are responsible for your actions and usage of this software. This indicates
  that your actions might, and could be, punishable by the law enforcement (police,
  FBI etc.). We, as the developers, cannot replace the actions that the law should
  do, but being vigilante and doing some actions that the law ***should*** do, when
  no actions are taken, could be done.
- Any actions, such as potential misuse, illegal activities or violations of
  Instagram's policies, are prohibited. By breaking the policies, you acknowledge
  that your account may or may not be banned, if this tool is being misused.
- We, the developers of this project are not responsible for any consequences or
  damages that might result from the use of this software. This project is designed
  for a specific purpose, being information gathering only. Using it for Blackmail
  or other things, may or may not be held reliable.

Please use the Osintgram project responsibly, respect privacy, maintain ethical
standards in all your activities, and use the tool with explicit permission. For
legal reasons, always have a legal document that you explicitly gained permission
for your actions.

---

## Potential Takedown
Another point to mention is that the original developer has all the right to tell
me, if I am required to archive, delete or completely dump the project. While I am
still happy on the development in this project, I am still trying to get in touch
with [the original developer](https://github.com/Datalux) himself, and hope to get
a positive response. The reason is that I do not want to dive myself into a lawsuit
due to copyrights. Even though that the Software Licensing exists, I am willing to
contact to ensure the legitimacy between me and the developer.

---

## Reasoning on Development
The first reason that I started the development on this project is to have the
stability of the Osintgram project back. Since the Instagram APIs have changed, and
a single API Call have been removed, the use of the
[original Project](https://github.com/Datalux/Osintgram) have broken the main
method, which the project can no longer start. This is due to a single line, which
I debugged, and will work with it, commented out. See
[the Restoration Process](OrigRestore.md).

Also, even though that Python is easy, I decided to switch to a different language.
It is none other than Java itself. Yes, I use Java and C++, by the way. However,
since I can be stubborn at some times, I decided to reject the offer that Gradle and
Maven suggests, and go with the more controllable form, writing my own Build Process
for the project. The reason that I don't use Maven or Gradle is because I have
problems with IntelliJ, when I need to handle multiple modules, or when I want to
separate packages into modules. It will always bug the IDE out, giving me headache
on where the problem is. And also, I use IntelliJ Ultimate and CLion, by the way.

To fill up the conception about me not using Gradle or Maven, I will be considering
on working an alternative way on building any type of project that will support
multiple languages that will like you using multiple modules instead of just one.
This will be a time-saver, especially that I might choose to dump the Shell scripts
for the Build Process, and use the new-style Building. I hear you, you don't like
bloat on your system, but we live in 2024, as of now.

Also, it might look like I bloated this project to the max, but I will try to keep
it lightweight as possible. Please do note that Java Development can receive more
and more bloat from time to time, since it can be time-consuming, or the fact that
Java is very much verbose and that it forces you into OOP (Object oriented
programming). The bloat should be more than an expectation.

Another point to mention is that I won't provide the direct builds for Windows via
the Batch / Powershell files, but I'll still provide the releases for the Windows
platform. It is meant to be run on Linux instead of on Windows, since I'm an Arch
Linux user, by the way.

---

## Build Process
To set up your workspace for the Osintgram project, you can select this project,
or [the original project](https://github.com/Datalux/Osintgram). However, this is
Osintgram4j project, which is a part of the OSINT and the Osintgram Family.

This project is mainly targeted for the Linux platform, meaning that on Windows or
macOS, you'll need a VM or a Cloud Shell Instance. A VM (Virtual Machine) is the
recommended option, if you don't want to waste your money on a Cloud Shell / Web
Server. On Windows, the recommended option is to use the WSL (Windows Subsystem
for Linux), since it is an optional feature on the latest Windows versions (10 &
11).

After having a Linux shell instance, you can run these following commands:
```shell
git clone https://github.com/BeChris100/osintgram4j && cd osintgram4j
chmod +x setup.sh build.sh
./setup.sh
./build.sh
```

Step-by-step explanation:
1. Via the `git` command, the project gets downloaded, and will automatically
   change the working directory into the `osintgram4j` folder.
2. The `chmod` command will set the Shell files executable. By default, `git`
   will automatically mark the Shell files executable, but might fail. Running
   them will make sure that the files are executable.
3. First Shell script (`setup.sh`) will create a working Build Environment. It
   will create a `build` folder, putting every necessary files, including external
   libraries (`org.json` is only currently involved) and prepare the necessary
   files.
4. Running `build.sh` script will invoke the necessary CXX (C/C++ Commands) and
   run the Java Compiler. Along the Java Compiler, the necessary resource files
   are also being copied. After that, the `jdeps`, `jlink` and `jpackage` are being
   executed, so that it generates an Application Package, making a native executable
   of this project. When that is complete, a prompt will be displayed, if the
   Application will be installed in `/usr/share/osintgram4j`, linking the binary
   to `/usr/bin/osintgram4j`, making a persistent binary execution from anywhere.

However, if you wish to not install directly, the Application Package will be stored
at the current working directory under `build/pkg/osintgram4j`, with the executable
being under `bin/osintgram4j`.

---

## Client Mods
The release of Osintgram4j Version v1, or rather pretty soon, v0.1, is introducing
a Modding API, which the original project does not have. Now, I wouldn't say that
it is a bad thing, but it is a cool feature. The use of client modifications is to
enhance the features that the stock Osintgram4j might not have delivered that you
might have expected.

While the use of Modifications is still a cool thing, you have to prioritize
Privacy, Data Safety and Security. That means, you should only download from sources
that I marked as "trusted", or from the original marketplace that does not exist
yet. Developers should follow the best practices to prevent the introduction of
Malware, Spyware, Ransomware or other software that might be harmful for yo' system.

Documentation on this GitHub Repository will be coming soon. Also, please, don't
mind that I'll use ChatGPT in some sections because sometimes, I just need help in
writing.

---

## Usage
Refer to [the Usage file](USAGE.md) for this project.