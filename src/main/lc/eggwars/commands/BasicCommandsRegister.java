package lc.eggwars.commands;

import org.bukkit.entity.Player;

import lc.eggwars.messages.Messages;
import lc.lcspigot.commands.CommandStorage;
import obed.me.minecore.objects.Jugador;

public final class BasicCommandsRegister {

    public void registerBasicCommands() {
        CommandStorage.register((sender, args) -> {
            if (sender instanceof Player) {
                final String format = Messages.get("levels.command");
                sender.sendMessage(format.replace("%level%", String.valueOf(Jugador.getJugador(sender.getName()).getServerStats().getStatsEggWars().getLevel())));
            }
        }, "level");

        CommandStorage.register((sender, args) -> Messages.send(sender, "help"), "help", "ayuda");
        CommandStorage.register((sender, args) -> sender.sendMessage("verhentai.top - 9/10 (Ofrece preview y una detallada sinopsis) \n hentaila.com (Comunidad activa, pero muy rara) - 7/10 \n nhentai.com (God pero ingles) 9-10 \n hentaird.com 7-10 (Muchos hentais viejos, con buena historia) \n muchohentai.com 8-10 (Ofrece episodios RAW y en otros idiomas) \n chochox.com 10-10 (De hecho, hay un comic en chochox que lo explica) \n De parte de ChocoMilk-senpai"), "hentai");
    }
}
