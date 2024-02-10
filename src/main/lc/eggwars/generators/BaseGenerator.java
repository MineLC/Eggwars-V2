package lc.eggwars.generators;

import net.minecraft.server.v1_8_R3.ItemStack;

public final record BaseGenerator(
    String key,
    String name,
    ItemStack drop,
    int maxlevel,
    Level[] levels
) {
    public static record Level(
        int upgradeItems,
        int waitingTime,
        int itemsToGenerate,
        int percentage,
        int refreshEvery
    ) {}
}