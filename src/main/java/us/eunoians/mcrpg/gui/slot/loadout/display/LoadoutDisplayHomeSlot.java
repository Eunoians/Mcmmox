package us.eunoians.mcrpg.gui.slot.loadout.display;

import com.diamonddagger590.mccore.gui.slot.Slot;
import com.diamonddagger590.mccore.player.CorePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.gui.loadout.display.LoadoutDisplayItemInputGui;
import us.eunoians.mcrpg.loadout.Loadout;

import java.util.List;

public class LoadoutDisplayHomeSlot extends Slot {

    private final Loadout loadout;

    public LoadoutDisplayHomeSlot(@NotNull Loadout loadout){
        this.loadout = loadout;
    }

    @Override
    public boolean onClick(@NotNull CorePlayer corePlayer, @NotNull ClickType clickType) {
        corePlayer.getAsBukkitPlayer().ifPresent(player -> {
            McRPG.getInstance().getGuiTracker().getOpenedGui(player).ifPresent(gui -> {
                if (gui instanceof LoadoutDisplayItemInputGui displayItemInputGui) {
                    displayItemInputGui.cancelSave();
                }
            });
            player.closeInventory();
        });
        return true;
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        MiniMessage miniMessage = McRPG.getInstance().getMiniMessage();
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(miniMessage.deserialize("<gold>Cancel editing loadout display item."));
        itemMeta.lore(List.of(miniMessage.deserialize("<gold>Click <gray>to cancel editing loadout display item.")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
