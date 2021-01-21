# About Biome Id Fixer by Desagas

###### [See the Wiki for all useful information (currently being updated).](https://github.com/desagas/Biome-Id-Fixer/wiki/). If you do not, I can't help you, as it explains very clearly how to install this mod, and some things that might happen.

###Next update will make this mod World Specific, as it is currently Pack Instance Specific.

###### WARNING: The exact same actions must be complete on BOTH the SERVER you are playing on, AND the CLIENT, otherwise it will simply make things look messed up, but server generation will win when generating new chunks.

This mod is very small, and very simple. Biome Id Fixer assigns the biome ids to a master id list, and calls from that list when minecraft needs to access the biome ids, for things like new generation, loading biome info for current generation, for sending to clients from servers, etc. It does not change the ids that are initially registered, only subsequent biome additions.

*   This was made with Forge 1.16.4.35.1.29, and works tested up to 1.16.5.36.0.1.
*   I have tested it with ATM6, 1.3.7 through 1.4.0, and it works fine, with ALL mods installed.
*   I have tested it on both Dedicated Servers and single player Clients.
*   Works with AE2 biomes, [Rats: Ratlantis](https://www.curseforge.com/minecraft/mc-mods/rats-ratlantis) biomes, [Oh the Biomes You Go (BYG)](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-youll-go) biomes, [Biomes o'Plenty (BoP)](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty) biomes, [Mahou Tsukai](https://www.curseforge.com/minecraft/mc-mods/mahou-tsukai) biomes, and Vanilla Minecraft biomes.
    
# How It Works
Biome Id Fixer uses the default forge/vanilla registry mechanic to get the originally assigned biome ids, pairing them with their default registry locations, and saves them in a master "**_biomeidfixer(doNOTedit).json_**" file for referencing to at a later time, say, when updating your ModPack to its next version, where **Oh the Biomes You Go** has added AMAZING nether quartz biomes!! It then uses this file to compare during every NEW world load on both servers and clients, and makes sure old biome numerical ids are not changed or overwritten.

* ###### This does prevent sharp biome edges from happening around new chunks. When you add biomes, there is only so much space to occupy, so Minecraft/Forge distribute biomes based on weight, which means that where one biome was, another one may exist when updateing. Same when removing biomes.
* ###### This does not change the new generation heat map at all. Deserts will still be registered as deserts, oceans as oceans, etc.
    
### Because of the way Biome Id Fixer works, you can transfer your Master BiomeIdFixer JSON to your Pack players, your Server Players, and your friend, if they lose theirs.
*   If you accidently delete your BiomeIdFix JSON, you could copy in one from somewhere else, or you can quite easily name a new one (described below). [See the Wiki for all useful information (currently being updated).](https://github.com/desagas/Biome-Id-Fixer/wiki/)
*   Should your pack or mod update add amazing new biomes, or should it remove them, Biome Id Fixer has already mapped them by the time the world loads, which means that ALL biome ids will NOT change, only new ones will be added, even if you remove mods with biomes.
*   If you decide to add back missing biomes at a later date, Minecraft/Forge will continue using the exact same biome map that was used before, provide no additional mods have been added. The re-added biomes will also use the same id that they used before.

## Help, I Delete my Map, Or, I Updated without this Installed
# &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dont Worry
No problem.

Say you started playing on version 1.3.7 of ATM6 and updated to 1.3.8, where Integrated Dynamics added their biome to the map, which shifted all other biome ids, **_AND you DID NOT have Biome Id Fixer_** installed. Here is how you fix it with this mod
###### Will only be successful for one version update when **_Biome Id Fixer was not previously installed_**. This can be from 1.3.7 to 1.3.8, or 1.3.5 to 1.3.9b, or 1.3.7 to 1.4.1, but only one update. The mod must have been installed on each version to go from 1.3.7 to 1.3.8 to 1.3.9b, etc.
1.  Do not touch your current pack, just shut it down.
1.  Download the ModPack you updated FROM (1.3.7 in this example), and make a new instance of it, either server or client, both files will be identical.
1. Download Biome Id Fixer and install in your mods folder in newly downloaded pack, and your current pack.
1.  Start the older version ModPack from the steps above.
1.  If you loaded a **_Client_** (the thingy you play on, with the GUI and graphics and all, even if playing on a server), not a Server (the actual program running the server), either load a single player world, or connect to your server. If you loaded a **_Dedicated Server,_** go to next step, as dedicated servers automatically load their worlds.
1.  In the MAIN directory of your older version ModPack, a file will now exist called **_"biomeidfixer(doNOTedit).json."_** Copy this file.
1.  Paste the above copied file into your **_CURRENT_** ModPack's main folder/directory.
1.  Start up your current modpack, and all biomes should now look and feel as they did before you updated, and newly added biomes will find new places in the world to spawn.

######  If not previously installed, as mentioned, this will only work successfully, for one version back. For example: Current is 1.4.1, you updated from 1.3.9.
###### If previously installed, this can go back as many versions as you want it to go, you just need to load each version in the order that YOU updated.


## _Chocolate Anyone ??_
**KEEP IT INSTALLED!!!**

Unlike other wonderfully tasty, delightful and sweet Chocolatety mods ;) that exist currently, Biome Id Fixer does not require that you visit every chunk you want to preserve existing biome data in, you need to only load the previous version's world **_once_**, in any biome, in any dimension, for a brief moment for the world to actually load, to save/preserve the BiomeIdFixer map of biomes.

**HOWEVER,** I **_highly recommend_** having [Chocolate Fix](https://www.curseforge.com/minecraft/mc-mods/chocolate-fix) installed as a precautionary step in reducing the minimal amount of effort it takes restoring after accidentally deleting your BiomeIdFixer JSON. Plus, two baskets for the same chocolate egg is better than one.

## Jargon
*   If biomes are removed, their associated ids will **NEVER** be used again, unless the mod containing the biomes is reinstalled, at which point the biomes will naturally take up those ids again.
*   **IMPORTANT NOTE:** If a mod changes the **_registry location_** (minecraft:plains) name of a biome, it will lose its assigned id, and be reassigned a new one. Forge/minecraft will choose how the previously generated biome in the world will act (temperature, weather, ambiance, etc).
*   **_How does Minecraft handle missing ids?_** When biomes are removed from Minecraft, Minecraft/Forge mechanics naturally "fill in" biomes that have biome ids that are no longer registered.
* This mod does not regenerate chunks that have already been loaded.

### Where to Do ...:

*   [Download this Mod from Curse here](https://www.curseforge.com/minecraft/mc-mods/biome-id-fixer), or on the launcher.
*   [The github is here](https://github.com/desagas/Biome-Id-Fixer).
*   [Learn more about using this mod?]()

### Known Bugs:

*  Endergetic seems to be registering its biome at different times on Servers and Clients. If you are using this mod, please copy the BiomeIdFixer(doNOTedit).json from your server to your client. This will fix the issue.

### What About ...:

*   having this be **WORLD SPECIFIC**: It will be coming in the next update. Currenly it is pack instance specific. If you have multiple worlds, or play or a Server and SP, make an instance for both.

### Can you ...:
*   Use it in your Pack?
    # &nbsp;&nbsp;&nbsp;&nbsp;YES!

### In need of recognition:
*   SkySom, he helped me a lot, when others literally banned me for inquiring.
*   HellfirePVP for having a great .gitignore AND build.gradle file to template off of.
*   MinecraftForge, Minecraft, and IntelliJ for making the documentation and shown code available under their license.
*   Tslat, you still helped me a lot.
