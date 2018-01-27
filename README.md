# README #

Indri is an Android UPnP control point.  It allows you to browse media exposed by UPnP/DLNA servers
on the local network, and start playback on media renderers.  For example, you can browse the
content on a server like Plex or Universal Media Server and start playback on your audio receiver.

In other words, it acts like a remote control for browsing and playing media on devices in the
local network.

### How do I get set up? ###

* Installation: Install to a physical Android device, not an emulator.  The Cling UPnP library does not work
well with emulators.  Read more about [Cling on the Android emulator](http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#chapter.Android).
* Configuration: There is no app-specific configuration to perform.  However, you must be connected to a WiFi
network with running media servers and media renderers.
* Dependencies: Indri uses the [Cling UPnP stack](https://github.com/4thline/cling)
