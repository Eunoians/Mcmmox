package us.eunoians.mcrpg.listener.entity.player;

import com.diamonddagger590.mccore.event.player.PlayerUnloadEvent;
import com.diamonddagger590.mccore.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;

/**
 * Handles unloading the user's data now that they've been saved
 */
public class CorePlayerUnloadListener implements Listener {

    @EventHandler
    public void onPlayerUnload(PlayerUnloadEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        McRPG mcRPG = McRPG.getInstance();
        if (corePlayer instanceof McRPGPlayer mcRPGPlayer) {
            mcRPGPlayer.asSkillHolder().cleanupHolder();
            mcRPG.getEntityManager().removeAbilityHolder(mcRPGPlayer.getUUID());
            mcRPG.getPlayerManager().removePlayer(mcRPGPlayer.getUUID());
            mcRPG.getDisplayManager().removeDisplay(mcRPGPlayer.getUUID());
        }

    }
}
