package it.impo.partySystem.api.database;

import it.impo.partySystem.api.data.Party;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PartyTable {

    public abstract void createTable() throws SQLException;

    public abstract CompletableFuture<Boolean> saveParty(UUID ownerUniqueID, Map<UUID, String> playerInParty);

    public abstract CompletableFuture<Boolean> removeParty(int id);

    public abstract CompletableFuture<Optional<Party>> getParty(int id);

    public abstract CompletableFuture<Map<Integer, Party>> loadPartyInCache();

}
