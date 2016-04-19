# Chisels & Bytes
Chisels &amp; Bytes is an addon for the Minecraft mod [Chisels &amp; Bits](http://mods.curse.com/mc-mods/minecraft/231095-chisels-bits) by AlgorithmX2. The mod allows you to export a Chisels & Bits block as a descriptive file for the [OpenComputers](http://mods.curse.com/mc-mods/minecraft/223008-opencomputers) 3d printer and works client-side only.

There are two prominent advantages of a 3D print over the Chisels & Bits block:
* Since the 3d printer is available in OpenComputers for Minecraft 1.7.10, this mod allows you, within limits, to port your Chisel & Bits creations back to 1.7.10.
* 3D prints have some features C&B blocks don't. First and foremost, they can have two states. Depending on the settings, you can emulate the behaviour of a door, switch or button. Second they can be set to emit light, no matter what they are made from. Finally you can set collision for each state. For more information install the ```print3d-examples``` package via ```oppm``` and look at the example file (```/usr/share/models/example.3dm```).

This mod requires Java 8.

## Usage
After installing the mod, hold a Chisels & Bits block in your hand and (by default) press the Next/Page Up key. The script will be copied to your clipboard and can be directly pasted into the in-game OpenComputers Editor or, out of game, into your favourite text editor for adjustment. The data is formatted for use with the ```print3d``` program available via the OpenComputers package manager ```oppm```.
If you have a Chisels & Bits block in the hotbar slot to the right of your active item, it will be used as the active state for the 3D print.

## Limits
* By default the OpenComputers 3D printer only allows 24 shapes per state. The mod tries to minimize the number of shapes, but complex blocks will probably go beyond that easily. The limit can be changed in the OpenComputers configuration file (```maxShapes``` in the ```printer``` section).
* If you port a design to 1.7.10 you will probably have to manually adjust the textures in the file. Textures are also not guaranteed to work 1.9 -> 1.8 (well, not even within 1.9 because at the time of this writing OpenComputers hasn't been ported yet)
* [1.9] Minecraft 1.9 removed the method I used to read the color of FlatColoredBlocks blocks, so I now have to specifically code for the mod. This can break at any update to FlatColoredBlocks but should cause crashes, only missing tint specification.
