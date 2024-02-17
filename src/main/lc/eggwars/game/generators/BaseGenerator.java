package lc.eggwars.game.generators;

import net.minecraft.server.ItemStack;

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