package lc.eggwars.others.tab;

import java.util.Collection;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import lc.eggwars.messages.Messages;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import obed.me.lccommons.api.entities.PlayerData;
import obed.me.lccommons.api.services.UserProvider;

public final class TabStorage {

    private static TabStorage storage;
    private final PacketPlayOutPlayerListHeaderFooter packetTab;

    TabStorage(PacketPlayOutPlayerListHeaderFooter packetTab) {
        this.packetTab = packetTab;
    }

    public void sendTab(final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.networkManager.handle(packetTab);
    }

    public void removePlayers(final Player bukkitPlayer, final Collection<Player> players) {
        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final EntityPlayer[] entityPlayers = new EntityPlayer[players.size()];
        int i = 0;

        for (final Player otherPlayer : players) {
            final EntityPlayer entityPlayer = ((CraftPlayer)otherPlayer).getHandle();
            entityPlayers[i++] = entityPlayer;
        } 
        player.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayers));
    }

    public void sendPlayerInfo(final Player bukkitPlayer, final Collection<Player> players) {
        final PlayerData pp = UserProvider.getInstance().getUserCache(bukkitPlayer.getName());
        final String playerInfo = Messages.color(pp.getRankInfo().getRank().getPrefix() + " &7" + pp.getRankInfo().getUserColor() + bukkitPlayer.getName());

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        player.listName = CraftChatMessage.fromString(playerInfo)[0];

        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player);
        final EntityPlayer[] entityPlayers = new EntityPlayer[players.size() + 1];
        entityPlayers[0] = player;
        int i = 1;

        for (final Player otherPlayer : players) {
            final EntityPlayer entityPlayer = ((CraftPlayer)otherPlayer).getHandle();
            entityPlayer.playerConnection.sendPacket(packet);
            entityPlayers[i++] = entityPlayer;
        } 
        player.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayers));
    }

    static void update(TabStorage newStorage) {
        storage = newStorage;
    }

    public static TabStorage getStorage() {
        return storage;
    }
}