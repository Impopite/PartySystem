package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.partySystem.PartySystem;

public class LeaveCommand {

    private final PartySystem plugin;

    public LeaveCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("leave")
                .executesPlayer((player, args) -> {
                    plugin.getPartyManager().partyLeave(player);
                });
    }
}
