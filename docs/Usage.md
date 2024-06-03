# Osintgram4j – Usage
After setting and building the code, you will be granted with the binaries provided
by `jpackage`. Those are Java Runtime Executables, which is a big help for the
end-users, by not being required to install JRE on the system

## Starting
In order to start Osintgram4j, you either need to fetch the Release Candidate from
the [Releases](https://github.com/BeChris100/osintgram4j/releases) tab, or compile
the source code by using the written scripts. Sure, compiling by hand is also
possible, but might take some time.

After using the Shell scripts to compile the source code, you should be able to
either run `./bin/osintgram4j`, or if decided to install globally, being able to
just run `osintgram4j` should be possible. To follow the tradition, you can also
put the target names. Let me provide some examples:

`osintgram4j target`: Initiates a connection to a specific target

`osintgram4j target1 target2 target3`: Initiates a connection to multiple targets
at once, allowing seamless connection between your account to them.

## Logging in / out
Obviously, you need an Instagram account. Preferably a spare account, in case that
you might be banned. However, I do not support, nor condone Ban evasions, as they
are against the Instagram's ToS, along with many other platforms that can initiate
banning accounts.

By logging in, you run `user-manager login`, following by either your Username or
E-Mail address. Optionally, you can pass in your password within its parameters,
but you will be prompted, if not given. For accounts with 2FA enabled, you might
also want to pass in your 2FA code, otherwise you will be prompted. Recommended
is the 2FA phone applications due to SIM swapping.

## Core Application Commands
To list all loaded commands, simply run `help`, Some default commands from the
[original project](https://github.com/Datalux/Osintgram) might also be ported, but
due to some limitations, I can't say for sure, whether that will be accomplished.
However, you won't error out easily in here due to more maintenance going on.

Anyway, let me list the interesting commands built into this application.

---

### System Interactive Shell
Running `sh` will spawn the default system command prompt, whether it is `bash` on
Linux, or `cmd.exe` on Windows. It is most likely to be used, if you need to sort
certain files, or need to read/modify certain files, if certain commands decided
to not give anything to the console.

---

### Session Control / Manager
***This part is still being worked on, since Instagram communications aren't in
place yet***

The fun part begins here: being able to manage multiple targets at once. By
running `sessionctl`, you can manage all saved targets, including the ones that
are already running.

---

### User Manager
***This part is still being worked on, since Instagram communications aren't in
place yet***

Another fun part of this project is being able to manage your own accounts,
in which you can manage the ability on logging into different accounts, switch
the Session Host from one account to another, and many more. Running `user-manager`
should do its trick.

---

### Application Client Management
Due to Modding support, along with being able to control certain things from the
Settings file itself, you are able to dynamically change the Settings, along with
managing the mods that you manage via installing / removing them. Managing the
Application state is with the command `clientmgr`, or simply running `settings` or
`mods`, which are aliases to the original command itself.

---

## Standard Commands
Some of the standard commands from the original project should also work. For that,
refer to [COMMANDS.md](https://github.com/Datalux/Osintgram/blob/master/doc/COMMANDS.md)
to guide through.