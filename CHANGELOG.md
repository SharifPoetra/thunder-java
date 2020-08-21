# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [1.20.5](https://github.com/SharifPoetra/thunder-java/compare/v1.20.4...v1.20.5) (2020-08-21)

### [1.20.4](https://github.com/SharifPoetra/thunder-java/compare/v1.20.3...v1.20.4) (2020-08-21)


### Bug Fixes

* some style fixes ([94af290](https://github.com/SharifPoetra/thunder-java/commit/94af290259108c5ca58d94f279ad39773dfa2223))


* enable gateway intent ([2caa5c3](https://github.com/SharifPoetra/thunder-java/commit/2caa5c3895a72f93886054f8bc0e4a36e7835128))
* **standard-version:** test ([3193b0b](https://github.com/SharifPoetra/thunder-java/commit/3193b0b6089137fea157219346039c6e465f6ecd))

### [1.20.3](https://github.com/SharifPoetra/thunder-java/compare/v1.20.2...v1.20.3) (2020-08-16)

### [1.20.2](https://github.com/SharifPoetra/thunder-java/compare/v1.20.1...v1.20.2) (2020-08-16)

### [1.20.1](https://github.com/SharifPoetra/thunder-java/compare/v1.20.0...v1.20.1) (2020-08-16)


### Bug Fixes

* movetrack command ([e3ef419](https://github.com/SharifPoetra/thunder-java/commit/e3ef41975636c7e53d59c5e4c11683655380a6a1))

## [1.22.0]
### Added
- Add `resetfilters` command
- Custom prefixes
- Add `prefix` command to set the custom prefix per guild

### Changed
- Rename `about` command to `stats` and improve the usage statistics

### Fixed
- Fixed where other filters won't work after activating bassboost filter
- Fixed bassboost command where it still can be used when there no playing music
- Fixed karaoke command where it still can be used when there no playing music
- Fixed nightcore command where it still can be used when there no playing music
- Fixed pitch command where it still can be used when there no playing music
- Fixed repeat command where it still can be used when there no playing music
- Fixed vaporwave command where it still can be used when there no playing music

## [1.20.0] - 2018-12-14
### Added
- Add `batslap` command
- Add `avatar` command
- Add `botstatus` command to change the bot current playing status
- Add `lewd` commamd on Interaction category

### Changed
- The changelog now sync with releases version
- Why is the version on the pom.xml is different on changelogs/releases? because the version is no matter for me and can be changed at every time and all we need is the compiled jar file to run it.
- Removed debug command
- Improve uptime on about command
- Option to specify where you want to save data files
- Add aliases for some commands on Music category

### Fixed
- About command fixes
- Fix the emote command where it always return the empty emoji information when there no arguments given
- Fix lyrics throws an error when there are no music playing and no arguments given
- Fix setinvcrole description

## [1.14.6] - 2018-11-6
### Added
- Add error message to every Interaction commands
- Add voice connections count to about command
- Add `dance` command
- Add `bobross` command
- Add `pat`s command
- Add `slap`s command
- Add `blush` command
- Interaction category
- Small image avatar to help command
- Add `cry` command

### Changed
- Kitsu command response is now have a color in embed
- Remove `uptime` command as it already in the `about` command
- The bot will be start typing before sending the image on Interaction commands

### Fixed
- Fix the bot won't leave the voice channel even the config is set to false! oh Finally!!


## [1.6.0] - 2018-10-10
### Added
- Add `setinvcrole` command that set a role to be given to the members when they're joining the voice channel
- Add `kitsu` command to search anime from https://kitsu.io

### Changed
- Improve emoji command using embed
- Add some stuff to about command
- Improve help command using embed and help for specified command


## [1.4.4] - 2018-10-5
### Added
- Add `about` command
- Add `pause` command
- Add `skipto` command
- Adding duration to start playing embed

### Fixed
- Fix emote command unknown emote ordered that one line length

### Changed
- Change success, warning and error emoji in response
- Change the nowplaying embed thumbnail to better quality

## [1.0.0] - 2018-10-3

- First release information can be found [on the releases](https://github.com/SharifPoetra/thunder-java/releases/tag/0.1.0).
