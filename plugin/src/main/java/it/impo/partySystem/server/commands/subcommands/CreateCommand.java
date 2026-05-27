package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.partySystem.PartySystem;
import it.impo.partySystem.api.config.constant.LangKey;

public class CreateCommand {

    private final PartySystem plugin;

    public CreateCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("create")
                .executesPlayer((player, args) -> {
                    if (plugin.getPartyManager().playerIsInParty(player)) {
                        player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.ALREADY_IN_PARTY));
                        return;
                    }
                    plugin.getPartyManager().createParty(player);
                });
    }
}
