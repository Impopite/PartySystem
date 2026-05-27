package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.partySystem.PartySystem;
import it.impo.partySystem.api.config.constant.LangKey;

public class DeleteCommand {

    private final PartySystem plugin;

    public DeleteCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("delete")
                .executesPlayer((player, args) -> {
                    if (plugin.getPartyManager().playerIsInParty(player)) {
                        player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.NOT_IN_PARTY));
                        return;
                    }
                    plugin.getPartyManager().removeParty(player);
                });
    }
}
