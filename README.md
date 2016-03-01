# Chisels & Bytes
Chisels &amp; Bytes is an addon for the Minecraft mod [Chisels &amp; Bits](http://mods.curse.com/mc-mods/minecraft/231095-chisels-bits) by AlgorithmX2. The mod allows you to export a Chisels & Bits block as a descriptive file for the [OpenComputers](http://mods.curse.com/mc-mods/minecraft/223008-opencomputers) 3d printer and works client-side only.

Since the 3d printer is available in OpenComputers for Minecraft 1.7.10, this mod allows you, within limits, to port your Chisel & Bits creations back to 1.7.10.

## Usage
After installing the mod, hold a Chisel & Bits block in your hand and (by default) press the Next/Page Up key. The script will be copied to your clipboard and can be directly pasted into the in-game OpenComputers Editor or, out of game, into your favourite text editor for adjustment. The data is formatted for use with the ```print3d``` program available via the OpenComputers package manager ```oppm```.

## Limits
* By default the OpenComputers 3D printer only allows 24 shapes. The mod tries to minimize the number of shapes, but complex blocks will probably go beyond that easily. The limit can be changed in the OpenComputers configuration file (```maxShapes``` in the ```printer``` section).
* If you port a design to 1.7.10 you will probably have to manually adjust the textures in the file.