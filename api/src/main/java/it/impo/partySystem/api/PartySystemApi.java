package it.impo.partySystem.api;

import it.impo.partySystem.api.config.ConfigLoader;
import it.impo.partySystem.api.database.PartyTable;
import it.impo.partySystem.api.manager.PartyManager;

public interface PartySystemApi {

    ConfigLoader getConfigLoader();

    PartyManager getPartyManager();

    PartyTable getPartyTable();
}
