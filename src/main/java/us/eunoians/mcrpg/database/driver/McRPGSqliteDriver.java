package us.eunoians.mcrpg.database.driver;

import com.diamonddagger590.mccore.database.driver.impl.SQLiteDatabaseDriver;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;

import java.io.File;

/**
 * A driver for McRPG to be able to use SQLite as a database
 */
public class McRPGSqliteDriver extends SQLiteDatabaseDriver {

    private final McRPG plugin;

    public McRPGSqliteDriver(@NotNull McRPG mcRPG) {
        this.plugin = mcRPG;
    }

    @NotNull
    @Override
    public String getPath() {
        File databaseFolder = new File(plugin.getDataFolder().getPath() + File.separator + "database");

        if (!databaseFolder.exists()) {
            databaseFolder.mkdir();
        }
        return databaseFolder.getPath() + File.separator + "mcrpg";
    }
}
