package us.eunoians.mcrpg.task.player;

import com.diamonddagger590.mccore.task.core.CancellableCoreTask;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This task will run continually in the background to save player data
 * on a timer.
 */
public class McRPGPlayerSaveTask extends CancellableCoreTask {

    public McRPGPlayerSaveTask(@NotNull McRPG plugin, double taskDelay, double taskFrequency) {
        super(plugin, taskDelay, taskFrequency);
    }

    @NotNull
    @Override
    public McRPG getPlugin() {
        return (McRPG) super.getPlugin();
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected void onDelayComplete() {

    }

    @Override
    protected void onIntervalStart() {

    }

    @Override
    protected void onIntervalComplete() {
        // Get a copy of all online players that need to be saved
        Set<McRPGPlayer> players = getPlugin().getPlayerManager().getAllPlayers().stream()
                .filter(corePlayer -> corePlayer instanceof McRPGPlayer)
                .map(corePlayer -> (McRPGPlayer) corePlayer)
                .collect(Collectors.toSet());
        try (Connection connection = getPlugin().getDatabase().getConnection()) {
            players.forEach(mcRPGPlayer -> mcRPGPlayer.savePlayer(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onIntervalPause() {

    }

    @Override
    protected void onIntervalResume() {

    }
}
