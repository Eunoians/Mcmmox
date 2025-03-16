package us.eunoians.mcrpg.task.experience;

import com.diamonddagger590.mccore.configuration.ReloadableContent;
import com.diamonddagger590.mccore.player.CorePlayer;
import com.diamonddagger590.mccore.task.core.CancellableCoreTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.configuration.FileType;
import us.eunoians.mcrpg.configuration.file.MainConfigFile;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.skill.experience.rested.RestedExperienceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * This task runs in the background and will periodically award experience
 */
public final class RestedExperienceAccumulationTask extends CancellableCoreTask {

    private final ReloadableContent<OnlineAccumulationType> onlineAccumulationType;
    private Set<UUID> playersLastUpdated;

    public RestedExperienceAccumulationTask(@NotNull McRPG mcRPG, double taskDelay, double taskFrequency) {
        super(mcRPG, taskDelay, taskFrequency);
        this.playersLastUpdated = new HashSet<>();
        this.onlineAccumulationType = new ReloadableContent<>(mcRPG.getFileManager().getFile(FileType.MAIN_CONFIG), MainConfigFile.RESTED_EXPERIENCE_ALLOW_ONLINE_ACCUMULATION,
                (yamlDocument, route) -> OnlineAccumulationType.fromString(yamlDocument.getString(route)).orElse(OnlineAccumulationType.DISABLED));
        mcRPG.getReloadableContentRegistry().trackReloadableContent(onlineAccumulationType);
    }

    @NotNull
    @Override
    public McRPG getPlugin() {
        return (McRPG) super.getPlugin();
    }

    @Override
    protected void onCancel() {
        playersLastUpdated.clear();
    }

    @Override
    protected void onDelayComplete() {

    }

    @Override
    protected void onIntervalStart() {

    }

    @Override
    protected void onIntervalComplete() {
        Set<UUID> currentPlayers = new HashSet<>();
        RestedExperienceManager restedExperienceManager = getPlugin().getRestedExperienceManager();
        double duration = getTaskFrequency();
        // Ensure we allow online accumulation
        if (onlineAccumulationType.getContent() != OnlineAccumulationType.DISABLED) {
            // Check all players
            for (CorePlayer corePlayer : getPlugin().getPlayerManager().getAllPlayers()) {
                currentPlayers.add(corePlayer.getUUID());
                // If the player was online last time, then we can award experience
                if (playersLastUpdated.contains(corePlayer.getUUID()) && corePlayer instanceof McRPGPlayer mcRPGPlayer) {
                    var playerOptional = mcRPGPlayer.getAsBukkitPlayer();
                    if (playerOptional.isPresent()) {
                        Player player = playerOptional.get();
                        boolean inSafeZone = getPlugin().getSafeZoneManager().isPlayerInSafeZone(player);
                        double accumulatedRestedExperience = 0;
                        // ENABLED and SAFE_ZONE_ONLY support safe zones so we can first check for
                        // safe zone accumulation, and then default to normal
                        if (mcRPGPlayer.isStandingInSafeZone() && inSafeZone && getPlugin().getFileManager().getFile(FileType.MAIN_CONFIG).getBoolean(MainConfigFile.SAFE_ZONE_ALLOW_ACCUMULATION)) {
                            accumulatedRestedExperience = restedExperienceManager.getRestedExperience((int) duration, true);
                        }
                        // Check to see if we support accumulation not in safe zones while players are online
                        else if (onlineAccumulationType.getContent() == OnlineAccumulationType.ENABLED) {
                            accumulatedRestedExperience = restedExperienceManager.getRestedExperience((int) duration, false);
                        }
                        restedExperienceManager.awardRestedExperience(mcRPGPlayer, accumulatedRestedExperience);
                        mcRPGPlayer.setStandingInSafeZone(inSafeZone);
                    }
                }
            }
            playersLastUpdated = currentPlayers;
        }
    }

    @Override
    protected void onIntervalPause() {

    }

    @Override
    protected void onIntervalResume() {

    }

    private enum OnlineAccumulationType {
        ENABLED,
        SAFE_ZONE_ONLY,
        DISABLED;

        public static Optional<OnlineAccumulationType> fromString(@NotNull String string) {
            return Arrays.stream(values()).filter(type -> type.toString().equalsIgnoreCase(string)).findFirst();
        }
    }
}
