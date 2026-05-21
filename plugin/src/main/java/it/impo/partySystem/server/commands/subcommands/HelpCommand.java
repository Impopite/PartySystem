package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public class HelpCommand {

    public CommandAPICommand get() {
        return new CommandAPICommand("help")
                .executesPlayer((player, args) -> {
                    sendHelp(player);
                });
    }

    // TODO -> restyle
    private void sendHelp(Player player) {
        player.sendMessage(text("§bᴘᴀʀᴛʏ ᴄᴏᴍᴍᴀɴᴅ ʜᴇʟᴘ:\n"));
        player.sendMessage("");
        player.sendMessage("§7/party create");
        player.sendMessage("§7/party invite <player>");
        player.sendMessage("§7/party leave");
        player.sendMessage("");
    }
}
