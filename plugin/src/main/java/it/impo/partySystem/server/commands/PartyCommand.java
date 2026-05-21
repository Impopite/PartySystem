package it.impo.partySystem.server.commands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.partySystem.PartySystem;
import it.impo.partySystem.server.commands.subcommands.*;

public class PartyCommand {

    private final PartySystem plugin;

    public PartyCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand[] get() {
        return new CommandAPICommand[]{
                create()
        };
    }

    protected CommandAPICommand create() {
        return new CommandAPICommand("party")
                .withSubcommands(
                        new HelpCommand().get(),
                        new CreateCommand(plugin).get(),
                        new DeleteCommand(plugin).get(),
                        new InviteCommand(plugin).get(),
                        new RemoveCommand(plugin).get(),
                        new InfoCommand(plugin).get()
                )
                .executesPlayer((player, args) -> {
                    player.sendMessage("§cUsa /party help");
                });
    }
}
