# Osintgram4j: OSINT for Instagram
Remember, how the beloved Osintgram project by [Datalux](https://github.com/Datalux)
fell apart due to Instagram's API changes and lack of updates? Well, you can stop
worrying. Osintgram4j aims to fix the issues, and make [the original
Project](https://github.com/Datalux/Osintgram) look like it was never broken. It's
a complete rewrite in Java, bringing back stability, along with new features.

***Note***: This project is still under development, with the original code being
analyzed, along with researches being made. Full functionality is not guaranteed
at this stage.

## Disclaimer & Privacy Policy
Osintgram4j, like any software, is intended for ethical and legal use only. Me and
other developers do not support unethical activities like hacking or other malicious
actions. Always follow relevant laws and Instagram's Terms of Service.

By using Osintgram4j, you acknowledge:
- **Responsibility**: You are accountable for your own actions.
- **Ethical Use**: Never misuse this tool or violate Instagram's policies.
- **No Liability**: We, the developers, are not liable for consequences arising from
  misuse.

With Privacy Policy in mind, you acknowledge:
- **Logging**: Your actions are logged locally that can be used for debugging.
- **No Server Uploads**: No data is being sent to any server without any explicit
  permission
- **Privacy Focus**: Sensitive information is not specifically recorded.

See the [full Privacy Policy](PrivacyPolicy.md) to review. Don't forget, use
Osintgram4j responsibly, respect privacy of others, and always act ethically.

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

1. **_Clone the repo_**: `git clone https://github.com/BeChris100/osintgram4j`
2. **_Set permissions_**: `chmod +x setup.sh build.sh`
3. **_Set up environment_**: `./setup.sh --force-download`
4. **_Build_**: `./build.sh`

The `--force-download` is an optional parameter. If given, it will always download
the JDK and the libraries, no matter its state. Otherwise, it will try to detect
JDK 21, and get the latest `org.json` library.

## Client Mods
Osintgram4j introduces a Modding API, which is something that I like. Having the
ability to modify the client directly instead of creating separate forks for each
modification is better than handling each fork, which could eventually make that fork
outdated from its original source. Allowing to customize and extend is functionality
is something anyone would want.

As always, prioritize privacy and security, along with downloading mods from trusted
sources.