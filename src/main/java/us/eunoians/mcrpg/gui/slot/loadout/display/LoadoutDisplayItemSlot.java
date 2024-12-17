package us.eunoians.mcrpg.gui.slot.loadout.display;

import com.diamonddagger590.mccore.gui.slot.Slot;
import com.diamonddagger590.mccore.player.CorePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.gui.loadout.display.LoadoutDisplayItemInputGui;
import us.eunoians.mcrpg.loadout.Loadout;

import java.util.List;

/**
 * This slot will open the {@link LoadoutDisplayItemInputGui} whenever clicked to allow
 * users to input an {@link ItemStack} to edit the {@link us.eunoians.mcrpg.loadout.LoadoutDisplay}.
 */
public class LoadoutDisplayItemSlot extends Slot {

    private final Loadout loadout;

    public LoadoutDisplayItemSlot(@NotNull Loadout loadout) {
        this.loadout = loadout;
    }

    @Override
    public boolean onClick(@NotNull CorePlayer corePlayer, @NotNull ClickType clickType) {
        Bukkit.broadcastMessage("1");
        corePlayer.getAsBukkitPlayer().ifPresent(player -> {
            Bukkit.broadcastMessage("2");
            if (corePlayer instanceof McRPGPlayer mcRPGPlayer) {
                Bukkit.broadcastMessage("3");
                LoadoutDisplayItemInputGui loadoutDisplayItemInputGui = new LoadoutDisplayItemInputGui(mcRPGPlayer, loadout);
                McRPG.getInstance().getGuiTracker().trackPlayerGui(mcRPGPlayer, loadoutDisplayItemInputGui);
                player.openInventory(loadoutDisplayItemInputGui.getInventory());
            }
        });
        return true;
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        MiniMessage miniMessage = McRPG.getInstance().getMiniMessage();
        ItemStack itemStack = loadout.getDisplay().getDisplayItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(miniMessage.deserialize("<gold>Loadout Display Item"));
        itemMeta.lore(List.of(miniMessage.deserialize("<gold>Click <gray>to change what item is used to display the loadout.")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
