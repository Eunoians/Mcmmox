package us.eunoians.mcrpg.loadout;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.eunoians.mcrpg.McRPG;

import java.util.Optional;

/**
 * A loadout display is the visual component to a {@link Loadout}, allowing users
 * to customize their individual loadouts.
 */
public final class LoadoutDisplay implements Cloneable {

    @NotNull
    private Material material;
    @Nullable
    private Integer customModelData;
    @Nullable
    private String displayName;

    public LoadoutDisplay(@NotNull ItemStack itemStack, @NotNull String displayName) {
        this.material = itemStack.getType();
        this.customModelData = itemStack.getItemMeta().hasItemName() ? itemStack.getItemMeta().getCustomModelData() : null;
        this.displayName = displayName;
    }

    public LoadoutDisplay(@NotNull Material material, @Nullable Integer customModelData, @Nullable String displayName) {
        this.material = material;
        this.customModelData = customModelData;
        this.displayName = displayName;
    }

    /**
     * Sets the {@link Material} and custom model data from the provided {@link ItemStack} to be used
     * for this display.
     *
     * @param itemStack The {@link ItemStack} to use as the display item.
     */
    public void setDisplayItem(@NotNull ItemStack itemStack) {
        material = itemStack.getType();
        customModelData = itemStack.getItemMeta().hasItemName() ? itemStack.getItemMeta().getCustomModelData() : null;
    }

    /**
     * Sets the {@link Material} to be used in this display.
     *
     * @param material The {@link Material} to be used in the display.
     */
    public void setDisplayItemType(@NotNull Material material) {
        this.material = material;
    }

    /**
     * Sets the custom model data to be used in this display.
     *
     * @param customModelData The custom model data to use in this display or {@code null} if none should
     *                        be used.
     */
    public void setDisplayItemModelData(@Nullable Integer customModelData) {
        this.customModelData = customModelData;
    }

    /**
     * Sets the display name for this display.
     *
     * @param displayName The {@link String} to use as a display name.
     */
    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    /**
     * The {@link Material} used by this display.
     *
     * @return The {@link Material} used by this display.
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the custom model data for the item used in the display.
     *
     * @return An {@link Optional} containing the custom model data for the item used in the display, or an
     * empty one if there is no model data.
     */
    @NotNull
    public Optional<Integer> getCustomModelData() {
        return Optional.ofNullable(customModelData);
    }

    /**
     * Gets the {@link String} used as the display name for the display.
     *
     * @return An {@link Optional} containing the {@link Optional} used as the display name for the display, or an empty
     * one if the default display name should be used.
     */
    @NotNull
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Constructs an {@link ItemStack} to use to display.
     *
     * @return An {@link ItemStack} to be used as a display.
     */
    @NotNull
    public ItemStack getDisplayItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (customModelData != null) {
            itemMeta.setCustomModelData(customModelData);
        }
        if (displayName != null) {
            itemMeta.displayName(getMcRPG().getMiniMessage().deserialize(displayName));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoadoutDisplay loadoutDisplay) {
            return getMaterial().equals(loadoutDisplay.getMaterial())
                    && getCustomModelData().equals(loadoutDisplay.getCustomModelData())
                    && getDisplayName().equals(loadoutDisplay.getDisplayName());
        }
        return false;
    }

    @NotNull
    @Override
    protected Object clone() {
        return new LoadoutDisplay(material, customModelData, displayName);
    }

    private McRPG getMcRPG() {
        return McRPG.getInstance();
    }
}
