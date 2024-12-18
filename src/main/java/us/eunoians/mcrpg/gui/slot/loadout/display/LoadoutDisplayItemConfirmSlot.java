package us.eunoians.mcrpg.gui.slot.loadout.display;

import com.diamonddagger590.mccore.gui.Gui;
import com.diamonddagger590.mccore.gui.slot.Slot;
import com.diamonddagger590.mccore.player.CorePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.gui.loadout.display.LoadoutDisplayItemInputGui;

import java.util.List;
import java.util.Set;

/**
 * This slot will close the {@link LoadoutDisplayItemInputGui} in order to save the display and reopen
 * the previous gui.
 */
public class LoadoutDisplayItemConfirmSlot extends Slot {

    public LoadoutDisplayItemConfirmSlot() {
    }

    @Override
    public boolean onClick(@NotNull CorePlayer corePlayer, @NotNull ClickType clickType) {
        // We close the inventory because we auto handle the saving on the close event there :>
        corePlayer.getAsBukkitPlayer().ifPresent(HumanEntity::closeInventory);
        return true;
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        MiniMessage miniMessage = McRPG.getInstance().getMiniMessage();
        ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(miniMessage.deserialize("<gold>Loadout Display Item"));
        itemMeta.lore(List.of(miniMessage.deserialize("<gold>Click <gray>to change what item is used to display the loadout.")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public Set<Class<? extends Gui>> getValidGuiTypes() {
        return Set.of(LoadoutDisplayItemInputGui.class);
    }
}
