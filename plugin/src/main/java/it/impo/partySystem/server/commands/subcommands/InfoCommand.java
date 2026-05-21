package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.partySystem.PartySystem;

public class InfoCommand {

    private final PartySystem plugin;

    public InfoCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("info")
                .executesPlayer((player, args) -> {
                    plugin.getPartyManager().partyInfo(player);
                });
    }
}
