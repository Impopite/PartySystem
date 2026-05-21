package it.impo.partySystem.api.manager;

import it.impo.partySystem.api.data.Party;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class PartyManager {

    public abstract void createParty(Player owner);

    public abstract void removeParty(Player owner);

    public abstract void addPlayerInParty(Player owner, Player target);

    public abstract void removePlayerInParty(Player owner, Player target);

    public abstract void partyInfo(Player player);

    public abstract boolean playerIsInParty(Player player);

    public abstract Map<Integer, Party> getPartyCache();
}
