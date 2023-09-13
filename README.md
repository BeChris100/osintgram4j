# osintgram4java

Remember on how old our old, beloved Osintgram project by Datalux barely even
functions? Yeah, ever since the lack of updates and API changes, the
[base repository](https://github.com/Datalux/Osintgram) of it barely works anymore.
For that, I decided to write a completely new repository, converting most of the
Python code into Java code.

---

## Reasoning on Development

The main reason on why the development started for this project is to have the
stability of the project Osintgram back, being kept up with the Instagram APIs and
make the setup easy (even though Python is easy, but Java is my favorite). But why
won't I go for Maven nor Gradle? Because in my own eyes, both of them suck,
especially on projects with multiple modules that I mainly make. Since they give me
headache, I do not work with those Build Systems, and will decide to make a better
alternative to those Build Systems.

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
Usage coming soon