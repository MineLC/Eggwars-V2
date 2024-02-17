package lc.eggwars.game.generators;

import net.minecraft.server.Entity;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

public final class GeneratorEntityItem extends Entity {

    public GeneratorEntityItem() {
        super(null);
        random = null;
    }

    public void setItemStack(ItemStack itemstack) {
        this.getDataWatcher().watch(10, itemstack);
        this.getDataWatcher().update(10);
    }

    @Override
    protected void a(NBTTagCompound arg0) {
    }

    @Override
    protected void b(NBTTagCompound arg0) {
    }

    @Override
    protected void h() {
        this.getDataWatcher().add(10, 5);
    }
}