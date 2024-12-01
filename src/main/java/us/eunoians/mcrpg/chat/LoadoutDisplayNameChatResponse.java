package us.eunoians.mcrpg.chat;

import com.diamonddagger590.mccore.chat.ChatResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.gui.loadout.display.LoadoutDisplayHomeGui;
import us.eunoians.mcrpg.loadout.Loadout;
import us.eunoians.mcrpg.loadout.LoadoutDisplay;

import java.util.UUID;

public class LoadoutDisplayNameChatResponse extends ChatResponse {

    private final Loadout loadout;

    public LoadoutDisplayNameChatResponse(@NotNull UUID chatterUUID, @NotNull Loadout loadout) {
        super(chatterUUID);
        this.loadout = loadout;
    }

    @Override
    public long getResponseWaitTime() {
        return 10;
    }

    @Override
    public void onResponse(@NotNull PlayerChatEvent playerChatEvent) {
        LoadoutDisplay loadoutDisplay = loadout.getDisplay();
        loadoutDisplay.setDisplayName(McRPG.getInstance().getMiniMessage().deserialize(playerChatEvent.getMessage()));
        Player player = playerChatEvent.getPlayer();
        McRPG.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(corePlayer -> {
            if (corePlayer instanceof McRPGPlayer mcRPGPlayer) {
                LoadoutDisplayHomeGui loadoutDisplayHomeGui = new LoadoutDisplayHomeGui(mcRPGPlayer, loadout);
                McRPG.getInstance().getGuiTracker().trackPlayerGui(mcRPGPlayer, loadoutDisplayHomeGui);
                player.openInventory(loadoutDisplayHomeGui.getInventory());
            }
        });
    }

    @Override
    public void onExpire() {

    }
}
