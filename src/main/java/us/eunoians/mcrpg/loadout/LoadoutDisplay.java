package us.eunoians.mcrpg.loadout;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LoadoutDisplay implements Cloneable {

    @NotNull
    private Material material;
    @Nullable
    private Integer customModelData;
    @Nullable
    private Component displayName;

    public LoadoutDisplay(@NotNull ItemStack itemStack, @NotNull Component displayName) {
        this.material = itemStack.getType();
        this.customModelData = itemStack.getItemMeta().hasItemName() ? itemStack.getItemMeta().getCustomModelData() : null;
        this.displayName = displayName;
    }

    public LoadoutDisplay(@NotNull Material material, @Nullable Integer customModelData, @Nullable Component displayName) {
        this.material = material;
        this.customModelData = customModelData;
        this.displayName = displayName;
    }

    public void setDisplayItem(@NotNull ItemStack itemStack) {
        material = itemStack.getType();
        customModelData = itemStack.getItemMeta().hasItemName() ? itemStack.getItemMeta().getCustomModelData() : null;
    }

    public void setDisplayItemType(@NotNull Material material) {
        this.material = material;
    }

    public void setDisplayItemModelData(@Nullable Integer customModelData) {
        this.customModelData = customModelData;
    }

    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public Optional<Integer> getCustomModelData() {
        return Optional.ofNullable(customModelData);
    }

    @NotNull
    public Optional<Component> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @NotNull
    public ItemStack getDisplayItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (customModelData != null) {
            itemMeta.setCustomModelData(customModelData);
        }
        if (displayName != null) {
            itemMeta.displayName(displayName);
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
    protected Object clone()  {
        return new LoadoutDisplay(material, customModelData, displayName);
    }
}
