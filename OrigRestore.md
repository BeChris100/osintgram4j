# Restore the project
Since the original project is no longer maintained, I thought about documenting for
the old project, being [Osintgram](https://github.com/Datalux/Osintgram) itself. Yes,
even though that it is no longer maintained, it is kind of still fixable.

This list (in this current case, only one) will guide you through on how to fix the
project itself. Note that:

- You already cloned the repository itself
  (`git clone https://github.com/Datalux/Osintgram`)
- You have a working environment running via `python3 -m venv .env`
- You already have installed all the necessary packages

## 404 Not Found on Connection
One of the common issues that everyone is facing (and still deciding to create the
exact same issue over and over again) is the "404 Not Found". This is due to a
specific API call that returns HTML Output, but expects a JSON File, meaning that
one, a 404 Error got occurred, and second, the JSON Parsing also threw an error,
meaning that the full connection is not possible.

To fix this issue, you have to open the `src/Osintgram.py` file in an Editor. In the
editor, there will be a function called `setTarget(self, target)`. In that method,
there is a specific call being made, being `self.check_following()`. This is the
exact error that fails. To make the program functional, simply comment that line out
by putting a Hashtag (`#`).

Before change:
```shell
self.following = self.check_following()
```

After change:
```shell
# self.following = self.check_following()
```

However, this will also disable the ability to target private accounts. If you wish
to still target private accounts, then you will have to wait, until a Stable
Release of Osintgram4j Version 1.0 is out. As of now, it is being close to be
pre-released with the Version of 0.1, with its core functionality being done.