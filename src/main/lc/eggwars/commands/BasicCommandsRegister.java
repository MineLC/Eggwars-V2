package lc.eggwars.commands;

import org.bukkit.entity.Player;

import lc.eggwars.database.PlayerDataStorage;
import lc.eggwars.messages.Messages;
import lc.lcspigot.commands.CommandStorage;

public final class BasicCommandsRegister {

    public void registerBasicCommands() {
        CommandStorage.register((sender, args) -> {
            if (sender instanceof Player player) {
                final String format = Messages.get("levels.command");
                sender.sendMessage(format.replace("%level%", String.valueOf(PlayerDataStorage.getStorage().get(player.getUniqueId()).level)));
            }
        }, "level");
        CommandStorage.register(new GiveCoin(), "givecoin");
        CommandStorage.register((sender, args) -> Messages.send(sender, "commands.help"), "help", "ayuda");
        CommandStorage.register((sender, args) -> sender.sendMessage(" verhentai.top §e9/10 §7(Ofrece preview y una detallada sinopsis) \n §fhentaila.com §e7/10 §7(Comunidad activa, pero muy rara) \n §fnhentai.com §e9-10 §7(God pero ingles) \n §fhentaird.com §e7-10 §7(Muchos hentais viejos, con buena historia) \n §fmuchohentai.com §e8-10 §7(Ofrece episodios RAW y en otros idiomas) \n §fchochox.com §e10-10 §7(De hecho, hay un comic en chochox que lo explica) \n \n §8by iChocoMilk (Lector de la biblia)"), "hentai");
    }
}
