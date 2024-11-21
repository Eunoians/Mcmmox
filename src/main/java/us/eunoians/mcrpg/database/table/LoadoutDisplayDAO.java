package us.eunoians.mcrpg.database.table;

import com.diamonddagger590.mccore.database.Database;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                "`loadout_uuid` varchar(36) NOT NULL," +
                "`display_material` varchar(32) NOT NULL," +
                "`custom_model_data` varchar(32) NOT NULL DEFAULT 0," +
                "`display_name` varchar(32) NULL," +
                "PRIMARY KEY (`loadout_uuid`, `ability_id`), " +
                // Ensure that the loadout is stored in the info table, also if it ever gets removed from that table, ensure it's deleted here
                "CONSTRAINT FK_loadout FOREIGN KEY (`loadout_uuid`) REFERENCES " + LoadoutInfoDAO.TABLE_NAME + "(`uuid`) ON DELETE CASCADE" +
                ");")) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
