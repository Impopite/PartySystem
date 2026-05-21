package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import it.impo.partySystem.PartySystem;
import it.impo.partySystem.api.config.constant.LangKey;
import org.bukkit.entity.Player;

public class InviteCommand {

    private final PartySystem plugin;

    public InviteCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("invite")
                .withArguments(new EntitySelectorArgument.OnePlayer("nome"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args.get(0);
                    if (target == null) {
                        player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PLAYER_NOT_FOUND));
                        return;
                    }

                    plugin.getPartyManager().addPlayerInParty(player, target);
                });
    }
}
