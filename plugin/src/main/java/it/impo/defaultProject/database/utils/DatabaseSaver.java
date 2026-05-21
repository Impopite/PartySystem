package it.impo.defaultProject.database.utils;

import it.impo.defaultProject.DefaultProject;

public class DatabaseSaver {

    private final DefaultProject plugin;

    public DatabaseSaver(DefaultProject plugin) {
        this.plugin = plugin;
        // MemberRepository memberRepository = new MemberRepository(plugin.getHikariCP().getDataSource(), plugin.getMemberCache());
    }

    public void saveAllCaches() {
        try {
            // plugin.getFactionRepository().saveAll().join();

            plugin.getLogger().info("Cache salvate correttamente.");
        } catch (Exception e) {
            plugin.getLogger().severe("Errore nel salvataggio cache: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
