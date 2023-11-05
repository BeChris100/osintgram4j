# OSINTgram (OSINT for Instagram)
Remember on how old our old, beloved Osintgram project by Datalux barely even
functions? Yeah, ever since the lack of updates and Instagram API changes, the
[base repository](https://github.com/Datalux/Osintgram) barely works anymore. For
that, I decided to rewrite the whole project in my favorite language of all time,
Java.

Note that this is still a work in the progress, as the original code is still
being analyzed, so that I can convert them into the functional Java code. It is
not expected to be fully functional as of now, as I recently started working on
Osintgram for Java.

---

## Disclaimer
Yes, I know that I used ChatGPT, but just bare with the disclaimer. It is necessary
to follow all laws, Terms of Service and to follow ethical guidelines.

This software, OSINTgram (OSINT for Instagram), is indented for legitimate and
ethical use only. Me, [the developer](https://github.com/Datalux) and other
contributors of the Osintgram project do not condone or support any form of
illegal, malicious, or unethical activities. It is essential to adhere to all
relevant laws and Instagram's ToS (Terms of Service) when using this software.

By using this project, you acknowledge and agree that:

- You are solely responsible for your actions and usage of this software.
- Any misuse, illegal activities, or violations of Instagram's policies are
 strictly prohibited.
- Me, the developer and other contributors of this project are not responsible for
 any consequences or damages resulting from the use of this software.

Please use OSINTgram responsibly, respect privacy, and maintain ethical standards
in all your activities.

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
The main reason on why the development started for this project is to have the
stability of the project Osintgram back, being kept up with the Instagram APIs and
make the setup easy (even though Python is easy, but Java is my favorite). But why
won't I go for Maven nor Gradle? Because in my own eyes, both of them suck,
especially on projects with multiple modules that I mainly make. Since they give me
headache, I do not work with those Build Systems, and will decide to make a better
alternative to those Build Systems. It will still use the Maven Repository, so no
worries.

Also, it might look like I bloated this project to the max, but I am still trying
to keep it as lightweight as possible. Note that Java Development can receive more
and more bloat from time to time, so this needs to be an expectation.

---

## Setup
To set up with the new Osintgram project, you need to do a few steps. On Windows,
use WSL, as the build process only supports Linux and macOS. However, you are able
to download the stable release of Osintgram from the Releases Page, due to the
complications within its Shell Script for the "Operating System Check" along with
its WSL Check.

(Note that the creation of an Application on the Instagram Developer Console
isn't relevant as of by the time of this update, but as soon as this Project
asks you to state your Application ID, then it will mean that it shifted towards
the Instagram default APIs.)

After creating the Application on the Instagram Developer Console, run the Shell
Script command to get an initialized state of this project on your machine by
running `setup.sh`. Afterward, run `build.sh` to compile the project, placing all
JAR files into the "build" directory. With that, it will generate the prepared
binaries for the execution, which should be located under "build/pkg". To run the
final build, do:

```shell
cd build/pkg
./bin/osintgram4j
```

What binaries were built there? The binaries that were built are produced by
`jpackage`. It is a tool that deploys executables without the requirement of
having to download the JRE/JDK. However, if you download the latest stable release
for macOS, you will have to run `osintgram4j.sh` because I do not own a MacBook
nor a macOS.

---

## Mods
This is a planned feature for the future, but as of now, it currently does not
support the use of Client Modifications. However, this also comes in Security Risks,
which also involve Malware, Spyware and other malicious Software that may come into
your hands. Always be on the lookout for any malicious Software.

Section coming soon.

---

## Usage
Refer to [the Usage file](USAGE.md) for this project.