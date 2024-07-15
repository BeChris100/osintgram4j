# OSINTgram (OSINT for Instagram)
```text
   ,#&@@@@%/       *%&@@@@&&%.        %&@%         /&&&&&&&&&*
 ,&@/     .%@/   /@%,     *&&,      *&/ @%              #@/   
 %@/       .%@* *@%.               (&,  @%              #@/   
.@&*        #@( (@#    .******   .&#.   @%              #@/
 (@(       *&@. .@@*    ..,#@*   /@@@@@@@@@(    @%      #@/
  *&&(,..*%@#.   .#@&(,..,/&@,          @&      @@#,. ,(@%.
     ,/((*.          ,/((/*.            ||        \*/(/,
```

Remember, how the beloved Osintgram project by [Datalux](https://github.com/Datalux)
fell apart due to Instagram's API changes and lack of updates? Well, you can stop
worrying. Osintgram4j aims to fix the issues, and make [the original
Project](https://github.com/Datalux/Osintgram) look like it was never broken. It's
a complete rewrite in Java, bringing back stability, along with new features.

---

## Use cases...?
No use cases yet. Matter of fact, this project is still heavily under development.
Keep an eye on the discussions tab, as I will be doing Announcements, when a new
milestone will be reached.

Oh, and when a project is under development, and features like not being able to log
into your Instagram account is unclear, then do note that features like these are not
fully adjusted yet that it is being used in general use case. And yes, people do
start to piss me off on how to use it. I am tired on telling that this tool is not
ready for public use yet.

---

## Disclaimer & Privacy Policy
Osintgram4j, like any other software, is intended for ethical and legal use only.
Other developers, myself included, do not support unethical activities like hacking
or other malicious actions. Always follow relevant laws and the platforms Terms of
Service.

By using Osintgram4j, you acknowledge:
- **Responsibility**: You are accountable for your own actions.
- **Ethical Use**: Never misuse this tool or violate Instagram's policies.
- **No Liability**: We, the developers, are not liable for consequences arising from
  misuse.

With Privacy Policy in mind, you acknowledge:
- **Logging**: Your actions are logged locally that can be used for debugging.
- **No Server Uploads**: No data is being sent to any server without any explicit
  permission
- **Sensitivity**: Some sensitive information is included, such as the commands that
  you type (e.g. usernames) in the interactive Shell

See the [full Privacy Policy](PrivacyPolicy.md) to review. Don't forget, use
Osintgram4j responsibly, respect privacy of others, and always act ethically.

---

## Build Process
Prepare a Linux environment, with either one of these options:
- Host (main OS / Dual Boot)
- VM (Virtual Machine)
- Cloud Shell / Servers (Google Cloud, Linode)
- Containers (Docker)
- WSL (Windows-only)

This project is mainly focused and optimized for **the Linux platform**. Simple
setup with the Shell scripts:

1. **_Download dependencies_**:
   - Debian/Ubuntu based: `sudo apt install build-essential cmake tar wget`
   - Arch/Manjaro based: `sudo pacman -Sy base-devel cmake tar wget`
2. **_Clone the repo_**: `git clone https://github.com/BeChris100/osintgram4j`
3. **_Set permissions_**: `chmod +x setup.sh build.sh`
4. **_Set up environment_**: `./setup.sh --force-download`
5. **_Build_**: `./build.sh`

The `--force-download` is an optional parameter. If given, it will always download
the JDK and the libraries, no matter its state. Otherwise, it will try to detect
JDK 21, and get the latest `org.json` and `org.apache:commons-cli` library.

Built binaries can be found under `./bin` directory (within the root source code
directory), which is a symlink towards `./out/pkg/osintgram4j/bin`.

Alternatively, you can install this project, and have the executables directly in the
PATH environment.

---

## Executables
By default, Osintgram4j delivers these following executable files:

- `osintgram4j`: The default executable to run this project
- `og4j-editor`: A C++ executable that requires administrative privileges, when
  applying new modifications
- `og4j-logdata`: An optional executable for Log File Handling (see "SLFT" for better
  explanation)

---

## SLFT
"SLFT", or rather, "Secure Log File Transfer", is used to transfer Log Files securely.
It is used to extract from the external Osintgram4j directory, but also being able to
encrypt the file itself, so that third-party people / bad actors don't have the ability
to peek in. Due to the nature of potential sensitive information being stored, this
will be a necessity, in case of being requested for a Log file.

Note that I will ***NOT*** ask you randomly for your Log files. Anyone that asks for
one of the files is more likely to be a scammer / hacker, looking for snatching your
information. In case that I will be asking, please look out for any ***red flags***
first. If that is the case that I will be asking for one of the log files, please
DO attach a link to the issue within the E-Mail or one of the other ***contact
methods***.

---

## Client Mods
Osintgram4j introduces a Modding API, which is something that I like. Having the
ability to modify the client directly instead of creating separate forks for each
modification is better than handling each fork, which could eventually make that fork
outdated from its original source. Allowing to customize and extend is functionality
is something anyone would want.

As always, prioritize privacy and security, along with downloading mods from trusted
sources.

---

## Usage
Refer to [the Usage file](docs/Usage) for this project.
