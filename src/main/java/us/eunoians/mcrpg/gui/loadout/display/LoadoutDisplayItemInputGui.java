package us.eunoians.mcrpg.gui.loadout.display;

import com.diamonddagger590.mccore.exception.CorePlayerOfflineException;
import com.diamonddagger590.mccore.exception.gui.InventoryAlreadyExistsForGuiException;
import com.diamonddagger590.mccore.gui.ClosableGui;
import com.diamonddagger590.mccore.gui.Gui;
import com.diamonddagger590.mccore.gui.slot.Slot;
import com.diamonddagger590.mccore.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.loadout.Loadout;

import java.util.Optional;
import java.util.Set;

public class LoadoutDisplayItemInputGui extends Gui implements ClosableGui {

    private static final Slot FILLER_GLASS_SLOT;
    private static final Slot PURPLE_GLASS_SLOT;
    private static final int INPUT_SLOT = 13;
    private static final Set<Integer> PURPLE_SLOTS = Set.of(INPUT_SLOT - 9, INPUT_SLOT - 1, INPUT_SLOT + 1, INPUT_SLOT + 9);
    private static final int RETURN_SLOT = 18;
    private static final int CONFIRM_SLOT = 26;

    // Create static slots
    static {
        // Create filler glass
        ItemStack fillerGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerGlassMeta = fillerGlass.getItemMeta();
        fillerGlassMeta.setDisplayName(" ");
        fillerGlass.setItemMeta(fillerGlassMeta);
        FILLER_GLASS_SLOT = new Slot() {

            @Override
            public boolean onClick(@NotNull CorePlayer corePlayer, @NotNull ClickType clickType) {
                return true;
            }

            @NotNull
            @Override
            public ItemStack getItem() {
                return fillerGlass;
            }
        };

        // Create purple glass
        ItemStack purpleGlass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta purpleGlassMeta = fillerGlass.getItemMeta();
        purpleGlassMeta.setDisplayName(" ");
        purpleGlass.setItemMeta(purpleGlassMeta);
        PURPLE_GLASS_SLOT = new Slot() {

            @Override
            public boolean onClick(@NotNull CorePlayer corePlayer, @NotNull ClickType clickType) {
                return true;
            }

            @NotNull
            @Override
            public ItemStack getItem() {
                return fillerGlass;
            }
        };
    }

    private final McRPGPlayer mcRPGPlayer;
    private final Player player;
    private final Loadout loadout;
    private boolean save = true;

    public LoadoutDisplayItemInputGui(@NotNull McRPGPlayer mcRPGPlayer, @NotNull Loadout loadout) {
        this.mcRPGPlayer = mcRPGPlayer;
        Optional<Player> playerOptional = mcRPGPlayer.getAsBukkitPlayer();
        if (playerOptional.isEmpty()) {
            throw new CorePlayerOfflineException(mcRPGPlayer);
        }
        this.player = playerOptional.get();
        this.loadout = loadout;
    }

    @Override
    protected void buildInventory() {
        if (this.inventory != null) {
            throw new InventoryAlreadyExistsForGuiException(this);
        } else {
            this.inventory = Bukkit.createInventory(player, 27, McRPG.getInstance().getMiniMessage().deserialize("<gold>Home Gui"));
            paintInventory();
        }
    }

    @Override
    public void paintInventory() {
        for (int i = 0; i < inventory.getSize(); i++) {
            setSlot(i, FILLER_GLASS_SLOT);
        }
        for (int i : PURPLE_SLOTS) {
            setSlot(i, PURPLE_GLASS_SLOT);
        }

    }

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, McRPG.getInstance());
    }

    @Override
    public void unregisterListeners() {
        InventoryClickEvent.getHandlerList().unregister(this);
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        if (save) {
            saveLoadoutDisplayItem();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(McRPG.getInstance(), () -> {
            LoadoutDisplayHomeGui loadoutDisplayHomeGui = new LoadoutDisplayHomeGui(mcRPGPlayer, loadout);
            McRPG.getInstance().getGuiTracker().trackPlayerGui(mcRPGPlayer, loadoutDisplayHomeGui);
            player.openInventory(loadoutDisplayHomeGui.getInventory());
        }, 1L);
    }

    public void saveLoadoutDisplayItem() {

    }

    public void cancelSave() {
        save = false;
    }
}
