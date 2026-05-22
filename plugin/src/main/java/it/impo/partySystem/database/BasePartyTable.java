package it.impo.partySystem.database;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.partySystem.api.data.Party;
import it.impo.partySystem.api.database.PartyTable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.intellij.lang.annotations.Language;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class BasePartyTable extends PartyTable {

    private final HikariDataSource dataSource;

    @Language("SQL")
    private static final String CREATE_PARTY_TABLE = """
            CREATE TABLE IF NOT EXISTS partysystem_party (
                id          INT             NOT NULL AUTO_INCREMENT PRIMARY KEY,
                ownerUUID   VARCHAR(36)     NOT NULL,
                members       TEXT          DEFAULT NULL
            );
            """;

    @Language("SQL")
    private static final String SAVE_PARTY = "INSERT INTO partysystem_party(ownerUUID, members) VALUES (?, ?);";

    @Language("SQL")
    private static final String REMOVE_PARTY = " DELETE FROM partysystem_party WHERE id = ?";

    @Language("SQL")
    private static final String GET_PARTY = " SELECT * FROM partysystem_party WHERE id = ?;";

    public BasePartyTable(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTable() throws SQLException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(CREATE_PARTY_TABLE)) {
            ps.execute();
        }
    }

    @Override
    public CompletableFuture<Boolean> saveParty(UUID ownerUUID, Map<UUID, String> playerInParty) {
        return supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SAVE_PARTY);
                statement.setString(1, ownerUUID.toString());

                if (playerInParty != null && !playerInParty.isEmpty()) {
                    String members = String.join(" , ", playerInParty.values());
                    statement.setString(2, members);
                } else  statement.setNull(2, Types.VARCHAR);

                int rows = statement.executeUpdate();
                return rows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> removeParty(int id) {
        return supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(REMOVE_PARTY, Statement.RETURN_GENERATED_KEYS);

                statement.setInt(1, id);
                statement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Optional<Party>> getParty(int id) {
        return supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(GET_PARTY, Statement.RETURN_GENERATED_KEYS);

                statement.setInt(1, id);

                ResultSet result = statement.executeQuery();
                if (!result.next()) return Optional.empty();
                return Optional.of(getPartyFromResult(result));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Map<Integer, Party>> loadPartyInCache() {
        return supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(GET_PARTY, Statement.RETURN_GENERATED_KEYS);

                Map<Integer, Party> cache = new HashMap<>();

                int id = 1;
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                int currentId = id;

                while (result.next()) {
                    cache.put(currentId, getPartyFromResult(result));
                    currentId++;
                    statement.setInt(1, currentId);
                    result = statement.executeQuery();
                }

                return cache;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Party getPartyFromResult(ResultSet result) throws SQLException {

        int id = result.getInt("id");
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("ownerUUID")));

        Map<UUID, String> partyMember = new HashMap<>();

        for (String member : result.getString("members").split(" , ")) {
            partyMember.put(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(member))).getUniqueId(), member);
        }

        return new Party(id, owner.getUniqueId(), partyMember);
    }
}
