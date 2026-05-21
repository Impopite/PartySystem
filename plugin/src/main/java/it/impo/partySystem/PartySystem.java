package it.impo.partySystem;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.partySystem.api.PartySystemApi;
import it.impo.partySystem.api.config.ConfigLoader;
import it.impo.partySystem.api.database.PartyTable;
import it.impo.partySystem.api.manager.PartyManager;
import it.impo.partySystem.config.BaseConfigLoader;
import it.impo.partySystem.database.BasePartyTable;
import it.impo.partySystem.database.utils.DatabaseCredentials;
import it.impo.partySystem.database.utils.HikariCP;
import it.impo.partySystem.manager.BasePartyManager;
import it.impo.partySystem.server.commands.PartyCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;

public final class PartySystem extends JavaPlugin implements PartySystemApi {

    @Getter
    private final String projectName = getDescription().getName();

    private ConfigLoader configLoader;
    private PartyManager partyManager;
    private PartyTable partyTable;

    @Getter
    private HikariCP hikariCP;
    @Getter
    private DatabaseCredentials databaseCredentials;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        getLogger().info("");
        getLogger().info(CYAN + "====================================" + RESET);
        getLogger().info(CYAN + projectName + RESET);
        getLogger().info(GRAY + "   Developed by " + WHITE + "zImpoo" + RESET);
        getLogger().info(CYAN + "====================================" + RESET);

        configLoader = new BaseConfigLoader(this);

        configLoader = new BaseConfigLoader(this);

        try {
            configLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        databaseCredentials = new DatabaseCredentials(this);
        hikariCP = new HikariCP(this, databaseCredentials);
        partyTable = new BasePartyTable(hikariCP.getDataSource());
        try {
            partyTable.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        partyManager = new BasePartyManager(this);
        partyTable.loadPartyInCache();


        PartyCommand partyCommand = new PartyCommand(this);
        for (CommandAPICommand command : partyCommand.get()) {
            command.register();
        }

        long took = System.currentTimeMillis() - start;

        getLogger().info(GREEN + "Commands loaded" + RESET);
        getLogger().info(GREEN + "Databases loaded" + RESET);
        getLogger().info(GREEN + "Config loaded" + RESET);
        getLogger().info("");
        getLogger().info(GREEN +  "enabled successfully in " + took + "ms" + RESET);
        getLogger().info(CYAN + "====================================" + RESET);

    }

    @Override
    public void onDisable() {
        if (hikariCP != null) {
            try {
                hikariCP.close();
            } catch (Exception e) {
                getLogger().warning("Error closing HikariCP: " + e.getMessage());
            }
        }

        getLogger().info("");
        getLogger().info(RED + "====================================" + RESET);
        getLogger().info(RED + projectName + RESET);
        getLogger().info(GRAY + "   Developed by " + WHITE + "zImpoo" + RESET);
        getLogger().info(RED + "====================================" + RESET);
        getLogger().info(RED + "Plugin disabled safely." + RESET);
        getLogger().info(RED + "====================================" + RESET);
    }

    @Override
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    @Override
    public PartyManager getPartyManager() {
        return partyManager;
    }

    @Override
    public PartyTable getPartyTable() {
        return partyTable;
    }

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[37m";
    private static final String WHITE = "\u001B[97m";
}
