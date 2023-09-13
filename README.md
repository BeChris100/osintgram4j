# OSINTgram (OSINT for Instagram)

Remember on how old our old, beloved Osintgram project by Datalux barely even
functions? Yeah, ever since the lack of updates and Instagram API changes, the
[base repository](https://github.com/Datalux/Osintgram) barely works anymore. For that, I decided to rewrite the
whole project in my favorite language of all time, Java.

Note that this is still a work in the progress, as the original code is still
being analyzed, so that I can convert them into the functional Java code. It is
not expected to be fully functional as of now, as I recently started working on
Osintgram for Java.

---

## Reasoning on Development

The main reason on why the development started for this project is to have the
stability of the project Osintgram back, being kept up with the Instagram APIs and
make the setup easy (even though Python is easy, but Java is my favorite). But why
won't I go for Maven nor Gradle? Because in my own eyes, both of them suck,
especially on projects with multiple modules that I mainly make. Since they give me
headache, I do not work with those Build Systems, and will decide to make a better
alternative to those Build Systems.

Also, it might look like I bloated this project to the max, but I am still trying
to keep it as lightweight as possible. Note that Java Development can receive more
and more bloat from time to time, so this needs to be an expectation.

---

## Setup
To set up with the new Osintgram project, you need to do a few steps. On Windows,
use WSL, as it will make things easier. Also with Windows, I am no longer a fan of
MS Windows due to it being privacy-concerned Operating System, and I am not willing
to work with that Operating System ever again.

First, run the Shell Script command to get an initialized state of this project on
your local machine by running `setup.sh`. Afterward, run `build.sh` to compile the
project, placing all JAR files into the "build" directory. With that, it will
generate the prepared binaries for the execution, which should be located under 
"build/pkg". To run the final build, do:

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

## Usage
Refer to [the Usage file](USAGE.md) for this project.