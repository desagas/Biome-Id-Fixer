# Official Changelogs

<details>
   <summary>Versions Control = 1.2.3.(i)4</summary>

   Prior releases are not 100% accurate to this structure.
   1. First Full Release, or World Breaking, Can't be Fixed.
   1. User Intervention Required.
   1. Changes to Forge Version, Significant Code Changes, or Feature Changes.
   1. Language, Logs, Names, Hotfixes, etc.
       1. For future use, to specify feature or release types.
</details>

## 1.0.1.s4
*   Removes around 80% of debug log spam.
*   Console log now show start and stop for forge events, includig file location and movement.
*   Removes unnecessary Forge event listener for *EntityJoinWorldEvent*.

## 1.0.1.s3
*   Lowers required Forge version to 1.16.5.36.1.16.
*   Updates *mods.toml* to reflect forge version change.

## 1.0.1.s2
###### _Hotfix Version_
*   Fixes server crash.

## 1.0.1.s1
###### _Version Archived_ - Significant Crash all Servers
*   Updates to Forge 36.1.23
*   Removes hardcoded folder and filepaths which fixes [#4](https://github.com/desagas/Biome-Id-Fixer/issues/4).
*   Coded for latest Minecraft / Forge code for 1.16.5.36.1.23

## 1.0.0.s1
*   Adjusts hardcoded folder and filepaths which fixes [#2](https://github.com/desagas/Biome-Id-Fixer/issues/2).

## 1.0.0.s0
###### _Version Archived_ - Significant Crash on Windows and Linux
*   Now world specific.
*   File *biomeidfixer(doNOTedit).json* must be moved from the modpack instance folder to a new folder called biomeidfixer, and then renamed:
*   New Location: instance/WorldName/biomeidfixer/BiomeIdFixer.json
*   No longer need to reload your pack to adjust biomes, simply reload the world. Moving from world to world does not require reloading the pack.

## 0.0.0.s2
*   Confirms working with and updates *mod.toml* to use Minecraft 1.16.5.

## 0.0.0.s1
*   Initial release, beta, fixes biomes ids being remapped when adding or removing other biomes.