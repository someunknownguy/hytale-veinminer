# hytale-veinminer
Permission driven veinminer for Hytale

Crouch to veinmine 

## Useful information for admins and personal use

Grant players permission 
    
    unknownrek.hytalemodding.veinmine.allowed

Or if you're not interest in that at all, change the mod config

    "LockedBehindPermission": false

and they will be able to veinmine using the tools you config while breaking the blocks you config

whitelist matching is case insensitive

Example config to be able to veinmine while holding any pickaxe or any hatchet

    "WhitelistHeldItemIds": [
        "Tool_Pickaxe_*",
        "tool_hatchet_*"
    ]


and to be able to only veinmine ores and wood blocks

    "WhitelistBlockIds": [
        "Ore_*",
        "Wood_*"
    ]

You can be more specific if you want the players to be able to only veinmine a specific wood type by completing that woods id