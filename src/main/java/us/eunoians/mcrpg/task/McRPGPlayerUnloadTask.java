package us.eunoians.mcrpg.task;

import com.diamonddagger590.mccore.database.table.impl.MutexDAO;
import com.diamonddagger590.mccore.database.transaction.BatchTransaction;
import com.diamonddagger590.mccore.database.transaction.FailsafeTransaction;
import com.diamonddagger590.mccore.player.CorePlayer;
import com.diamonddagger590.mccore.player.PlayerManager;
import com.diamonddagger590.mccore.task.PlayerUnloadTask;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.database.table.LoadoutDAO;
import us.eunoians.mcrpg.database.table.LoadoutInfoDAO;
import us.eunoians.mcrpg.database.table.PlayerSettingDAO;
import us.eunoians.mcrpg.database.table.SkillDAO;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * A task used to save and unload the player data
 */
public class McRPGPlayerUnloadTask extends PlayerUnloadTask {

    public McRPGPlayerUnloadTask(@NotNull McRPG mcRPG, @NotNull McRPGPlayer mcRPGPlayer) {
        super(mcRPG, mcRPGPlayer);
    }

    @Override
    public McRPGPlayer getCorePlayer() {
        return (McRPGPlayer) super.getCorePlayer();
    }

    @NotNull
    @Override
    public McRPG getPlugin() {
        return (McRPG) super.getPlugin();
    }

    @Override
    protected boolean unloadPlayer() {
        PlayerManager playerManager = getPlugin().getPlayerManager();
        // Ensure they are registered... probs a better way to do this
        Optional<CorePlayer> corePlayerOptional = playerManager.getPlayer(getCorePlayer().getUUID());

        if (corePlayerOptional.isPresent() && corePlayerOptional.get() instanceof McRPGPlayer mcRPGPlayer) {
            SkillHolder skillHolder = mcRPGPlayer.asSkillHolder();

            try (Connection connection = getPlugin().getDatabase().getConnection()) {
                BatchTransaction batchTransaction = new BatchTransaction(connection);
                FailsafeTransaction failsafeTransaction = new FailsafeTransaction(connection);
                failsafeTransaction.addAll(SkillDAO.saveAllSkillHolderInformation(connection, skillHolder));
                failsafeTransaction.addAll(LoadoutInfoDAO.saveAllLoadoutInfo(connection, skillHolder));
                failsafeTransaction.addAll(LoadoutDAO.saveAllLoadouts(connection, skillHolder));
                batchTransaction.addAll(PlayerSettingDAO.savePlayerSettings(connection, getCorePlayer().getUUID(), getCorePlayer().getPlayerSettings()));
                failsafeTransaction.executeTransaction();
                batchTransaction.executeTransaction();

                if (mcRPGPlayer.useMutex()) {
                    MutexDAO.updateUserMutex(connection, mcRPGPlayer.getUUID(), false);
                }
                return true;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onTaskExpire() {

    }

    @Override
    protected void onDelayComplete() {

    }

    @Override
    protected void onIntervalStart() {

    }

    @Override
    protected void onIntervalPause() {

    }

    @Override
    protected void onIntervalResume() {

    }
}
