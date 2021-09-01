# About BiomeIdFixer

###### [See the Wiki for more useful information](https://github.com/desagas/Biome-Id-Fixer/wiki/).
___
BiomeIdFixer intercepts Minecraft's and Forge's biome registration processes, records the assigned ids, and then denies Minecraft the ability to change them if new biomes are added or current ones are removed. Minecraft then uses the recorded biome ids identically to how it would have without this mod installed.


## Adventure Mode Creators
___
For adventure and story mode style mapmakers: cut away scenes are the best, as long as they are not too long: *Final Fantasy III, USA,* when the air strike destroyed the land; *Zelda, OoT* where ash is floating down or horrible enemies now exist around the Temple of Time; *Zelda, BoTW,* around the castle and up the mountain turn dark as Ganondorf grows stronger, well ... imagine a script that kicks your players momentarily, loads another biomeidfixer.json map of biome ids, and then reloads your world, [where the once peaceful plains are now filled with Ghasts (click to see video showing this)](https://www.youtube.com/watch?v=O8qa0LHJYKA&ab_channel=DenverWilliamDenverWilliam) and Wither skeletons (not spawning due to light levels) ... you can do that. Remember, this affects **ALL** existing chunks, which is perfect, because these worlds are usually man made.


## For Everyone
___
### Biome Ids are world specific, and changes only require worlds to be restarted, not your Minecraft instance.
*   Made with Forge 1.16.4.35.1.29, and works tested up to 1.16.5.36.1.23.
*   Works with ATM6 1.3.7 through 1.6.2, Create Together, and Enigmatica 6, and many more packs.
*   Tested on both dedicated **servers** and single player **clients**. 
*   Personally tested on Windows and macOS (May 21, 2021), and others confirm Linux.
*   Works with all Vanilla biomes, as well as those from [Applied Energistic 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2), [Rats: Ratlantis](https://www.curseforge.com/minecraft/mc-mods/rats-ratlantis), [Oh the Biomes You Go (BYG)](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-youll-go), [Biomes o'Plenty (BoP)](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty), [Mahou Tsukai](https://www.curseforge.com/minecraft/mc-mods/mahou-tsukai), [The Endergetic Expansion](https://www.curseforge.com/minecraft/mc-mods/endergetic), [The Twilight Forest](https://www.curseforge.com/minecraft/mc-mods/the-twilight-forest), and many more.


### You can transfer your master biomeidfixer.json file to your pack players, your server players, and your friends, if they lose theirs, or you simply want to share.
*   If you add a biome, BiomeIdFixer adds the new biome id to the master list.
*   If you remove a biome, nothing happens. Its id is forever locked to that biome, whether still in your modpack or not, so, it can **never** be overwritten by Minecraft. 
*   So, even if you accidentally delete your master biomemap file, you could copy one from someone or somewhere else, or you can quite easily let Minecraft generate a new one. [See the Wiki for more useful information, including restoring your biomeidfixer.json file.](https://github.com/desagas/Biome-Id-Fixer/wiki/).
*   **Warning!**: if you did not start your pack on the same version as someone else, and update through the versions in the exact same order as they have, your biome ids may not have been assigned identically, so, you do run the risk of ids still not being 100% correct. In other words, **always back up your world folders!!**
*   If, however, you started on the same pack version, and updated to the same versions, time and time again, your biomemaps will be identical, and you should be able to transfer them from one person/world to another.


### Jargon, In the Know
*   If you remove a biome and decide to add it back at a later time, Minecraft and Forge will continue using the exact same biome map that was used before you removed it, provide no additional biomes have been added, and their weights have not changed. These re-added biomes will continue to use the exact same id that they used before. *This is great for accidental mod deletions or config changes.*
*   If a mod changes a **_biome's name_**, the biome will lose its assigned id and be assigned a new one for its new name. Minecraft will then choose how to handle the previously generated biome, including temperature, weather, ambiance, mobs, etc. This is unavoidable, unpreventable, and without reverting the name back, unfixable.
*   BiomeIdFixer does not regenerate chunks that have already been generated, it makes sure that biomes that have already been assigned remain the biome they were initially generated as, as long as that biome still exists in the game.


### Warning, Sharp Edges

*   This does not prevent sharp biome edges from separating old and new chunks. Minecraft generates a noise map that is specific to your world seed, and biomes are assigned to that map, filling it complete. When you add a biome, the remaining uncliamed map space will be distributed amoungst more biomes, so it is very likely that these will occur.


## Where to Do ...:
___
*   [Download this Mod from Curse, here](https://www.curseforge.com/minecraft/mc-mods/biome-id-fixer), or on the Twitch, Curse, or Overwolf launcher.
*   [The github is here](https://github.com/desagas/Biome-Id-Fixer).
*   [Learn more about using this mod!](https://github.com/desagas/Biome-Id-Fixer/wiki/)


## Known Bugs:
___
*   The Endergetic Expansion seems to be registering its biome at different times on Servers and Clients. If you are using this mod, please copy the *WorldName/biomeidfixer/biomeidfixer.json* file from your server to your client. This should fix the issue.
*   If the file *server.properties* on a server instnace, or *biomeidfixer.temp* on a singleplayer instance are removed while the world is loading or being created, the entire map will be replaced with whatever new map has been registered by Minecraft or Forge. This will be classed as user error, and, for the most part, can not be fixed. I do not plan on implementing a backup solution. This file can not be moved into your world folder, because of the way Minecraft's biome registration works.


## What About ...:
___
*   ... manually turning off individual biomes, preventing them from being registered entirely, within one simple config file for all installed mods? In the future, I may implement this.
*   ... controlling the other aspects of biomes in the exact same way, changing the sky color, temperature, humidity, mobs that can spawn, etc. In the future, I may implement this.
*   ... controlling and modifying every little thing that minecraft registers while loading into a world? It can be done, and in the future, I may implement this.
*   ... overloading currently loaded biome ids, allowing you to change how a biome acts without even having to exit your world? If I have the headspace, and a few hundred hours, it definitely would be possible, but would be super complicated to learn how to code it.


### Can you ...:
*   ... use it in your Pack?
    # &nbsp;&nbsp;&nbsp;&nbsp;YES!

## In Need of Recognition:
___
*   SkySom, you helped me a lot, when others literally banned me for even inquiring.
*   HellfirePVP for having a great .gitignore AND build.gradle file to template off of.
*   MinecraftForge, Minecraft, and IntelliJ for making the documentation and shown code available under their license.
*   Tslat, you helped me a lot.
*   TeamPneumatic for having code that showed me how to save to the world folder.
