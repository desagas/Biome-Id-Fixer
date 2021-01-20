# About Biome Id Fixer by Desagas

This mod is very small, and very simple. Biome Id Fixer assigns the biome ids to a master id list, and calls from that list when minecraft needs to access the biome ids.

*   This was made with Forge 1.16.4.35.1.29, and works tested up to ..36.
*   I have tested it with ATM6, 1.3.7 through 1.4.0, and it works fine, with 246 mods installed.
*   I have tested it on both Servers and single player clients.
*   Works with AE2 biomes, Rats biomes, Oh the Biomes You Go (BYG) biomes, Biomes o'Plenty biomes, Mahou Tsukai biomes, and Vanilla Minecraft biomes.
    
# How It Works
Biome Id Fixer uses the default forge/registry mechanic to get the originally assigned biome ids, pairing them with their default registry locations, and saves them in a master "**_biomeidfixer(doNOTedit).json_**" file for referencing to at a later time, say, when updating your ModPack to its next version, where **Oh the Biomes You Go** has added AMAZING nether quartz biomes!! It then uses this file to compare during every NEW world load on both servers and clients.

This DOES NOT stop sharp biome edges from happening around new chunks. When you add biomes, there is only so much space to occupy, so Minecraft/Forge distribute biomes based on weight, which means that where one biome was, another one may exist when updateing. Same when removing biomes.
    
### Because of the way Biome Id Fixer works, you can transfer your Master BiomeMap to other Minecraft Systems/Servers.
*   If you accidently delete your biomeMap, you could copy in one from somewhere else, or you can quite easily name a new one (described below).
*   Should your update add amazing new biomes, or should it remove them, Biome Id Fixer has already mapped them by the time the world starts, which means that ALL biome ids will NOT change, only new ones will be added, even if you remove mods with biomes.
*   If you decide to add back the missing biomes at a later date, Minecraft/Forge will continue using the exact same biome map that was used before, provide no additional mods have been added.

## Help, I Delete my Map, Or, I Updated without this Installed
# &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dont Worry
No problem.

Say you updated from version 1.3.7 of ATM6 to 1.3.8, where Integrated Dynamics added their biome to the map, which shifted all other ids, Here is how you fix it.
1.  Do not touch your current pack, just shut it down.
1.  Download the ModPack you updated FROM (1.3.7 in this example), and make a new instance of it, either server or client, both files will be identical.
1.  Start the downloaded ModPack downloaded in the step above.
1.  If you loaded a **_Client_** (the thingy you play on, with the GUI and graphics and all, even if playing on a server), not a Server (the actual program running the server), either load a single player world, or connect to your server. If you loaded a **_Server,_** go to next step, as servers automatically load their worlds.
1.  In the MAIN directory of your ModPack, a file will now exist called **_"biomeidfixer(doNOTedit).json."_** Copy this file.
1.  Paste the above copied file into your **_NEW_** ModPack main folder/directory.
1.  Start up your modpack, and all biomes will now look and feel as they did before you updated, and newly added biomes will find new places in the world to spawn.

## _Chocolate Anyone ??_
**KEEP IT INSTALLED!!!**

Unlike other wonderfully tasty, delightful and sweet Chocolatety mods ;) that exist currently, Biome Id Fixer does not require that you visit every chunk you want to preserve existing biome data in, you need to only load the **OLD** world **_once_**, in any biome, in any dimension, for a brief moment for the world to actually load, to save/preserve the BiomeIdMap.

**HOWEVER,** I **_highly recommend_** having [Chocolate Fix](https://www.curseforge.com/minecraft/mc-mods/chocolate-fix) installed as a precautionary step in reducing the minimal amount of effort it takes restoring after accidentally deleting your biomeMap. Plus, two baskets from the same egg is better than one.

## Jargie
*   If biomes are removed, their associated ids will **NEVER** be used again, unless the mod containing the biomes is reinstalled, at which point the biomes will naturally take up those ids again.
*   **IMPORTANT NOTE:** If a mod changes the **_registry location_** (minecraft:plains) name of a biome, it will lose its assigned id, and be reassigned a new one.
*   **_How does Minecraft handle missing ids?_** When biomes are removed from Minecraft, Minecraft/Forge mechanics naturally "fill in" biomes that have biome ids that are no longer registered.
* This mod does not regenerate chunks that have already been loaded.

### Where to Do ...:

*   [Download this Mod from Curse here](https://www.curseforge.com/minecraft/mc-mods/biome-id-fixer), or on the launcher.
*   [The github is here](https://github.com/desagas/Biome-Id-Fixer).

### Known Bugs:

*  

### What About ...:

*   

### Can you ...:
*   Use it in your Pack?
    # &nbsp;&nbsp;&nbsp;&nbsp;YES!

### In need of recognition:
*   SkySom, he helped me a lot, when others literally banned me for inquiring.
*   HellfirePVP for having a great .gitignore AND build.gradle file to template off of.
*   MinecraftForge, Minecraft, and IntelliJ for making the documentation and shown code available under their license.
*   Tslat, you still helped me a lot.
