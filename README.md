# About Biome Id Fixer by Desagas

###### [See the Wiki for all useful information](https://github.com/desagas/Biome-Id-Fixer/wiki/).

Biome Id Fixer assigns the biome ids to a master id list, and calls from that list when minecraft needs to access the biome ids, for things like new generation, loading biome info for current generation, for sending to clients from servers, etc. It does not change the ids that are initially registered, only subsequent biome additions.

###Next update will make this mod World Specific, as it is currently Pack Instance Specific.

*   This was made with Forge 1.16.4.35.1.29, and works tested up to 1.16.5.36.0.1.
*   I have tested it with ATM6, 1.3.7 through 1.4.0, and it works fine, with ALL mods installed.
*   I have tested it on both Dedicated Servers and single player Clients.
*   Works with AE2 biomes, [Rats: Ratlantis](https://www.curseforge.com/minecraft/mc-mods/rats-ratlantis) biomes, [Oh the Biomes You Go (BYG)](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-youll-go) biomes, [Biomes o'Plenty (BoP)](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty) biomes, [Mahou Tsukai](https://www.curseforge.com/minecraft/mc-mods/mahou-tsukai) biomes, and Vanilla Minecraft biomes.


* ###### This does not prevent sharp biome edges from happening around new chunks. When you add biomes, there is only so much space to occupy, so Minecraft/Forge distribute biomes based on weight, which means that where one biome was, another one may exist when updateing. Same when removing biomes.

    
### Because of the way Biome Id Fixer works, you can transfer your Master BiomeIdFixer JSON to your Pack players, your Server Players, and your friend, if they lose theirs.
*   If you accidently delete your BiomeIdFix JSON, you could copy in one from somewhere else, or you can quite easily name a new one. [See the Wiki for all useful information, including restoring your biomeidfixer JSON.](https://github.com/desagas/Biome-Id-Fixer/wiki/)
*   Should your pack or mod update add amazing new biomes, or should it remove them, Biome Id Fixer has already mapped them by the time the world loads, which means that ALL biome ids will NOT change, only new ones will be added, even if you remove mods with biomes.
*   If you decide to add back missing biomes at a later date, Minecraft/Forge will continue using the exact same biome map that was used before, provide no additional mods have been added. The re-added biomes will also use the same id that they used before.

## _Chocolate Anyone ??_


Unlike other wonderfully tasty, delightful and sweet Chocolatety mods ;) that exist currently, Biome Id Fixer does not require that you visit every chunk you want to preserve existing biome data in, you need to only load the previous version's world **_once_**, in any biome, in any dimension, for a brief moment for the world to actually load, to save/preserve the BiomeIdFixer map of biomes.

**HOWEVER,** I **_ recommend_** having [Chocolate Fix](https://www.curseforge.com/minecraft/mc-mods/chocolate-fix) installed as a precautionary step in reducing the minimal amount of effort it takes restoring after accidentally deleting your BiomeIdFixer JSON. Plus, two baskets for the same chocolate egg is better than one.

## Jargon
*   When biomes are removed, their associated ids will **NEVER** be used again, unless the mod containing the biomes is reinstalled, at which point the biomes will naturally take up those ids again.
*   When a mod changes the **_registry location_** (minecraft:plains) name of a biome, it will lose its assigned id, and be reassigned a new one. Forge/minecraft will choose how the previously generated biome in the world will act (temperature, weather, ambiance, etc).
*   Biome Id Fixer does not regenerate chunks that have already been loaded.

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
*   TeamPneumatic for having code that showed me how to save to the world.
