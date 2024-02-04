package lc.eggwars.generators;

import org.bukkit.Material;

public final record BaseGenerator (
    String key,
    String name,
    Level[] levels,
    Material drop,
    int maxLevel
) {

    public static record Level(
        int itemsNeedToLevel,
        int secondsToGenerate,
        int amountGenerated
    ) {}
}