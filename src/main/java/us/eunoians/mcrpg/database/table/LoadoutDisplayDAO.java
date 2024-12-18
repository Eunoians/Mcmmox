package us.eunoians.mcrpg.database.table;

import com.diamonddagger590.mccore.database.Database;
import com.diamonddagger590.mccore.database.table.impl.TableVersionHistoryDAO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.entity.holder.LoadoutHolder;
import us.eunoians.mcrpg.loadout.Loadout;
import us.eunoians.mcrpg.loadout.LoadoutDisplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This DAO is used to store and access a {@link us.eunoians.mcrpg.loadout.Loadout}'s
 * {@link us.eunoians.mcrpg.loadout.LoadoutDisplay}.
 */
public class LoadoutDisplayDAO {

    static final String TABLE_NAME = "mcrpg_loadout_display";
    private static final int CURRENT_TABLE_VERSION = 1;

    /**
     * Attempts to create a new table for this DAO provided that the table does not already exist.
     *
     * @param connection      The {@link Connection} to use to attempt the creation
     * @param database The {@link Database} being used to attempt to create the table
     * @return {@code true} if a new table was made or {@code false} otherwise.
     */
    public static boolean attemptCreateTable(@NotNull Connection connection, @NotNull Database database) {
        //Check to see if the table already exists
        if (database.tableExists(connection, TABLE_NAME)) {
            return false;
        }

        /*****
         ** Table Description:
         ** Contains player loadout slots
         *
         *
         * loadout_id is the slot of the player's loadout the data belongs to
         * uuid is an integer representing the slot in the loadout that the ability is stored in
         * ability_id is the ability id that is used to find the corresponding {@link UnlockedAbilities} value
         **
         ** Reasoning for structure:
         ** PK is the composite of `loadout_id` field, `slot_number` and `uuid`, as a loadout id will be present once for each ability in the loadout,
         * so the combination of that and the slot number will be used to look up individual abilities and each player uuid is unique.
         *
         * The foreign key requires the player's uuid to be present in the loadout table as that's where the player's loadout info is stored
         *****/
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE `" + TABLE_NAME + "`" +
                "(" +
                "`holder_uuid` varchar(36) NOT NULL," +
                "`loadout_id` int(11) NOT NULL DEFAULT 1," +
                "`display_material` varchar(32) NOT NULL," +
                "`custom_model_data` varchar(32) NOT NULL DEFAULT 0," +
                "`display_name` varchar(32) NULL," +
                "PRIMARY KEY (`holder_uuid`, `loadout_id`), " +
                // Ensure that the loadout is stored in the info table, also if it ever gets removed from that table, ensure it's deleted here
                "CONSTRAINT FK_loadout FOREIGN KEY (`holder_uuid`, `loadout_id`) REFERENCES " + LoadoutInfoDAO.TABLE_NAME + " (`holder_uuid`, `loadout_id`) ON DELETE CASCADE" +
                ");")) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks to see if there are any version differences from the live version of this SQL table and then current version.
     * <p>
     * If there are any differences, it will iteratively go through and update through each version to ensure the database is
     * safe to run queries on.
     *
     * @param connection The {@link Connection} that will be used to run the changes
     */
    public static void updateTable(@NotNull Connection connection) {
        int lastStoredVersion = TableVersionHistoryDAO.getLatestVersion(connection, TABLE_NAME);
        if (lastStoredVersion < CURRENT_TABLE_VERSION) {
            //Adds table to our tracking
            if (lastStoredVersion == 0) {
                // Create an index to group by UUIDs
                try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE INDEX holder_uuid_index_loadout_display ON " + TABLE_NAME + " (holder_uuid)")) {
                    preparedStatement.executeUpdate();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                TableVersionHistoryDAO.setTableVersion(connection, TABLE_NAME, 1);
                lastStoredVersion = 1;
            }
        }
    }

    @NotNull
    public static List<PreparedStatement> saveAllLoadoutDisplays(@NotNull Connection connection, @NotNull LoadoutHolder loadoutHolder) {
        List<PreparedStatement> preparedStatements = new ArrayList<>();
        for (int i = 1; i <= loadoutHolder.getMaxLoadoutAmount(); i++) {
            preparedStatements.addAll(deleteLoadoutDisplay(connection, loadoutHolder.getUUID(), i));
            preparedStatements.addAll(saveLoadoutDisplay(connection, loadoutHolder.getUUID(), loadoutHolder.getLoadout(i)));
        }
        return preparedStatements;
    }

    @NotNull
    public static List<PreparedStatement> saveLoadoutDisplay(@NotNull Connection connection, @NotNull UUID loadoutHolderUUID, @NotNull Loadout loadout) {
        return loadout.shouldSaveDisplay() ? saveLoadoutDisplay(connection, loadoutHolderUUID, loadout.getLoadoutSlot(), loadout.getDisplay()) : List.of();
    }

    @NotNull
    public static List<PreparedStatement> saveLoadoutDisplay(@NotNull Connection connection, @NotNull UUID loadoutHolderUUID, int loadoutSlot, @NotNull LoadoutDisplay loadoutDisplay) {
        List<PreparedStatement> statements = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (holder_uuid, loadout_id, display_material, custom_model_data, display_name) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, loadoutHolderUUID.toString());
            preparedStatement.setInt(2, loadoutSlot);
            preparedStatement.setString(3, loadoutDisplay.getMaterial().toString());
            preparedStatement.setInt(4, loadoutDisplay.getCustomModelData().orElse(0));
            preparedStatement.setString(5, loadoutDisplay.getDisplayName().isPresent() ? loadoutDisplay.getDisplayName().get() : null);
            statements.add(preparedStatement);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return statements;
    }

    @NotNull
    public static List<PreparedStatement> deleteLoadoutDisplay(@NotNull Connection connection, @NotNull UUID loadoutHolderUUID, int loadoutSlot) {
        List<PreparedStatement> statements = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE holder_uuid = ? AND loadout_id = ?");
            preparedStatement.setString(1, loadoutHolderUUID.toString());
            preparedStatement.setInt(2, loadoutSlot);
            statements.add(preparedStatement);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return statements;
    }

    @NotNull
    public static Optional<LoadoutDisplay> getLoadoutDisplay(@NotNull Connection connection, @NotNull UUID loadoutHolderUUID, int loadoutSlot) {
        Optional<LoadoutDisplay> loadoutDisplayOptional = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT display_material, custom_model_data, display_name FROM " + TABLE_NAME + " WHERE holder_uuid = ? AND loadout_id = ?");) {
            preparedStatement.setString(1, loadoutHolderUUID.toString());
            preparedStatement.setInt(2, loadoutSlot);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Material material = Material.getMaterial(resultSet.getString("display_material"));
                int customModelData = resultSet.getInt("custom_model_data");
                String displayName = resultSet.getString("display_name");
                if (material != null) {
                    loadoutDisplayOptional = Optional.of(new LoadoutDisplay(material, customModelData, displayName));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return loadoutDisplayOptional;
    }
}
