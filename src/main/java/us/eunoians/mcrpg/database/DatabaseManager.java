package us.eunoians.mcrpg.database;

import com.cyr1en.flatdb.Database;
import com.cyr1en.flatdb.DatabaseBuilder;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.database.tables.TableVersionHistoryDAO;
import us.eunoians.mcrpg.database.tables.skills.ArcheryDAO;
import us.eunoians.mcrpg.database.tables.skills.AxesDAO;
import us.eunoians.mcrpg.database.tables.skills.ExcavationDAO;
import us.eunoians.mcrpg.database.tables.skills.FishingDAO;
import us.eunoians.mcrpg.database.tables.skills.FitnessDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private McRPG instance;
    private Database database;
    private final ThreadPoolExecutor databaseExecutorService =
            new ThreadPoolExecutor(1, 4, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<>());


    public DatabaseManager(McRPG plugin) {
        this.instance = plugin;

        DatabaseBuilder dbBuilder = new DatabaseBuilder();
        dbBuilder.setDatabasePrefix("mcrpg_");
        dbBuilder.setPath(plugin.getDataFolder().getAbsolutePath() + "/database/mcrpg");
        try {
            database = dbBuilder.build();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        attemptCreateTables();
        //TODO Fire events to allow custom DAO's to be created from 3rd party plugins?

    }

    /**
     * Gets the {@link Database} that is being used
     *
     * @return The {@link Database} that is being used
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Gets the {@link ThreadPoolExecutor} used to run database queries
     *
     * @return The {@link ThreadPoolExecutor} used to run database queries
     */
    public ThreadPoolExecutor getDatabaseExecutorService() {
        return databaseExecutorService;
    }

    private CompletableFuture<Void> attemptCreateTables() {

        Connection connection = database.getConnection();
        Logger logger = McRPG.getInstance().getLogger();

        CompletableFuture<Void> tableCreationFuture = new CompletableFuture<>();
        getDatabaseExecutorService().submit(() -> {

            //We need to create this first to ensure that versioning is setup
            TableVersionHistoryDAO.attemptCreateTable(connection, this).thenAccept(tableVersionHistoryTableCreated -> {

                logger.log(Level.INFO, "Database Creation - Table Version History DAO "
                                       + (tableVersionHistoryTableCreated ? "created a new table." : "already existed so skipping creation."));

                //Can now start creating skill tables since the table version has already been created
                ArcheryDAO.attemptCreateTable(connection, this).thenAccept(archeryTableCreated -> logger.log(Level.INFO, "Database Creation - Archery DAO "
                                                                                                                         + (archeryTableCreated ? "created a new table." : "already existed so skipping creation.")));
                AxesDAO.attemptCreateTable(connection, this).thenAccept(axesTableCreated -> logger.log(Level.INFO, "Database Creation - Axes DAO "
                                                                                                                      + (axesTableCreated ? "created a new table." : "already existed so skipping creation.")));
                ExcavationDAO.attemptCreateTable(connection, this).thenAccept(excavationTableCreated -> logger.log(Level.INFO, "Database Creation - Excavation DAO "
                                                                                                                               + (excavationTableCreated ? "created a new table." : "already existed so skipping creation.")));
                FishingDAO.attemptCreateTable(connection, this).thenAccept(fishingTableCreated -> logger.log(Level.INFO, "Database Creation - Fishing DAO "
                                                                                                                            + (fishingTableCreated ? "created a new table." : "already existed so skipping creation.")));
                FitnessDAO.attemptCreateTable(connection, this).thenAccept(fitnessTableCreated -> logger.log(Level.INFO, "Database Creation - Fitness DAO "
                                                                                                                         + (fitnessTableCreated ? "created a new table." : "already existed so skipping creation.")));

                tableCreationFuture.complete(null);
            });
        });

        return tableCreationFuture;

    }

    private void updateTables() {

        Connection connection = database.getConnection();

        //We need to first update the table version history and then we can update other tables
        TableVersionHistoryDAO.updateTable(connection).thenAccept(thisIsNullThouLol -> {

        });
    }
}
