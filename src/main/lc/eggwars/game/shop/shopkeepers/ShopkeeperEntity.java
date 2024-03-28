package lc.eggwars.game.shop.shopkeepers;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.World;

public final class ShopkeeperEntity extends EntityLiving {

    public ShopkeeperEntity(final World world) {
        super(world);
    }

    @Override
    protected void h() {
        super.h();
        this.datawatcher.a(12, (byte)0);
    }

    @Override
    public ItemStack bA() {
        return new ItemStack(Item.getById(1));
    }

    @Override
    public ItemStack[] getEquipment() {
        return new ItemStack[5];
    }

    @Override
    public ItemStack getEquipment(int arg0) {
        return new ItemStack(Item.getById(1));
    }

    @Override
    public void setEquipment(int arg0, ItemStack arg1) {
    }
}