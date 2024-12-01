package us.eunoians.mcrpg.gui.slot.loadout.display;


import com.diamonddagger590.mccore.gui.slot.Slot;
import com.diamonddagger590.mccore.player.CorePlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.chat.LoadoutDisplayNameChatResponse;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.loadout.Loadout;

public class LoadoutDisplayNameEditSlot extends Slot {

    private final McRPGPlayer mcRPGPlayer;
    private final Loadout loadout;

    public LoadoutDisplayNameEditSlot(@NotNull McRPGPlayer mcRPGPlayer, @NotNull Loadout loadout) {
        this.mcRPGPlayer = mcRPGPlayer;
        this.loadout = loadout;
    }

    @Override
    public boolean onClick(@NotNull CorePlayer corePlayer, @NotNull ClickType clickType) {
        corePlayer.getAsBukkitPlayer().ifPresent(player -> {
            // Close inventory
            player.closeInventory();
            // Notify player to send a response for the new name of the loadout
            McRPG mcRPG = McRPG.getInstance();
            MiniMessage miniMessage = mcRPG.getMiniMessage();
            Audience audience = mcRPG.getAdventure().player(player);
            audience.sendMessage(miniMessage.deserialize("<gray>Please type in chat the name you want this loadout to be called, or type <gold>cancel</gold> to cancel:"));
            LoadoutDisplayNameChatResponse loadoutDisplayNameChatResponse = new LoadoutDisplayNameChatResponse(player.getUniqueId(), loadout);
            mcRPG.getChatResponseManager().addPendingResponse(player.getUniqueId(), loadoutDisplayNameChatResponse);
        });
        return true;
    }
}
