package it.impo.partySystem.server.commands.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import it.impo.partySystem.PartySystem;
import it.impo.partySystem.api.config.constant.LangKey;
import it.impo.partySystem.api.data.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class RemoveCommand {

    private final PartySystem plugin;

    public RemoveCommand(PartySystem plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("remove")
                .withArguments(partyMembersArgument("target"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args.get("target");
                    if (target == null) {
                        player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PLAYER_NOT_FOUND));
                        return;
                    }

                    plugin.getPartyManager().removePlayerInParty(player, target);
                });
    }

    public Argument<Player> partyMembersArgument(String name) {
        return new EntitySelectorArgument.OnePlayer(name)
                .replaceSuggestions(ArgumentSuggestions.strings(info -> {

                    Player sender = (Player) info.sender();
                    Party party = plugin.getPartyManager().getPartyCache().values().stream()
                            .filter(p -> p.ownerUUID().equals(sender.getUniqueId()))
                            .findFirst()
                            .orElse(null);

                    if (party == null || party.members() == null) return new String[0];

                    return party.members().keySet().stream()
                            .map(Bukkit::getPlayer)
                            .filter(Objects::nonNull)
                            .map(Player::getName)
                            .toArray(String[]::new);
                }));
    }
}
