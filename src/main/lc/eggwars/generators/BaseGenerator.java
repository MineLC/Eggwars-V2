package lc.eggwars.generators;

import net.minecraft.server.v1_8_R3.ItemStack;

public final record BaseGenerator(
    String key,
    String name,
    Level[] levels,
    ItemStack item,
    int maxLevel
) {

    public static record Level(
        int itemsNeedToLevel,
        int secondsToGenerate,
        int amountGenerated
    ) {}
}