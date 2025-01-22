package us.eunoians.mcrpg.task.player;

import com.diamonddagger590.mccore.player.CorePlayer;
import com.diamonddagger590.mccore.player.PlayerManager;
import com.diamonddagger590.mccore.task.player.PlayerUnloadTask;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
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
                mcRPGPlayer.savePlayer(connection);
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
