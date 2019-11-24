# Changelog

Key: [+] added, [-] removed, [~] modified, [!] important.

## v1.21.6
- [~] Fixed where other filters won't work after activating bassboost filter
- [+] Add `resetfilters` command
- [~] Fixed bassboost command where it still can be used when there no playing music
- [~] Fixed karaoke command where it still can be used when there no playing music
- [~] Fixed nightcore command where it still can be used when there no playing music
- [~] Fixed pitch command where it still can be used when there no playing music
- [~] Fixed repeat command where it still can be used when there no playing music
- [~] Fixed vaporwave command where it still can be used when there no playing music

## v1.20.0
- [~] About command fixes
- [!] The changelog now sync with releases version
- [!] Why is the version on the pom.xml is different on changelogs/releases? because the version is no matter for me and can be changed at every time and all we need is the compiled jar file to run it.
- [~] Fix the emote command where it always return the empty emoji information when there no arguments given
- [~] Fix lyrics throws an error when there are no music playing and no arguments given
- [+] Add `lewd` commamd on Interaction category
- [~] Fix setinvcrole description
- [-] Removed debug command
- [~] Improve uptime on about command
- [+] Add `botstatus` command to change the bot current playing status
- [!] Option to specify where you want to save data files
- [~] Add aliases for some commands on Music category
- [+] Add `batslap` command
- [+] Add `avatar` command

## v1.14.6

- [~] kitsu command response is now have a color in embed
- [+] Interaction category
- [+] add `pat`s command
- [+] add `slap`s command
- [+] add `blush` command
- [+] add `cry` command
- [+] small image avatar to help command
- [~] add voice connections count to about command
- [!] fix the bot won't leave the voice channel even the config is set to false! oh Finally!!
- [-] remove `uptime` command as it already in the `about` command
- [~] add error message to every Interaction commands
- [~] the bot will be start typing before sending the image on Interaction commands
- [+] add `dance` command
- [+] add `bobross` command

## v1.6.0

- [~] Improve emoji command using embed
- [!] Add some stuff to about command
- [~] Improve help command using embed and help for specified command
- [+] add `setinvcrole` command that set a role to be given to the members when they're joining the voice channel
- [+] add `kitsu` command to search anime from https://kitsu.io

## v1.4.4

- [~] Fix emote command unknown emote ordered that one line length
- [+] Add `about` command
- [~] Change success, warning and error emoji in response
- [+] Add `pause` command
- [+] Add `skipto` command
- [~] Change the nowplaying embed thumbnail to better quality
- [~] Adding thumbnail and footer source to start playing embed
- [+] Adding duration to start playing embed

## v1.0.0

- [!] First release information can be found [on the releases](https://github.com/SharifPoetra/thunder-java/releases/tag/0.1.0).
