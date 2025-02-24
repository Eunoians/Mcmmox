package us.eunoians.mcrpg.entity.player;

import com.diamonddagger590.mccore.database.transaction.BatchTransaction;
import com.diamonddagger590.mccore.database.transaction.FailSafeTransaction;
import com.diamonddagger590.mccore.player.CorePlayer;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.ability.attribute.AbilityAttributeManager;
import us.eunoians.mcrpg.ability.attribute.AbilityTierAttribute;
import us.eunoians.mcrpg.ability.attribute.AbilityUpgradeQuestAttribute;
import us.eunoians.mcrpg.ability.impl.TierableAbility;
import us.eunoians.mcrpg.database.table.LoadoutAbilityDAO;
import us.eunoians.mcrpg.database.table.LoadoutDisplayDAO;
import us.eunoians.mcrpg.database.table.LoadoutInfoDAO;
import us.eunoians.mcrpg.database.table.PlayerExperienceExtrasDAO;
import us.eunoians.mcrpg.database.table.PlayerSettingDAO;
import us.eunoians.mcrpg.database.table.SkillDAO;
import us.eunoians.mcrpg.entity.holder.QuestHolder;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.event.setting.PlayerSettingChangeEvent;
import us.eunoians.mcrpg.quest.Quest;
import us.eunoians.mcrpg.quest.QuestManager;
import us.eunoians.mcrpg.setting.PlayerSetting;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The main "player" object for any player who will be playing McRPG.
 * <p>
 * This is also the main access point to a player's skill data through
 * {@link #asSkillHolder()}
 */
public class McRPGPlayer extends CorePlayer {

    private final McRPG mcRPG;
    private final SkillHolder skillHolder;
    private final QuestHolder questHolder;
    private final Map<NamespacedKey, PlayerSetting> playerSettings;
    private final PlayerExperienceExtras playerExperienceExtras;

    public McRPGPlayer(@NotNull Player player, @NotNull McRPG mcRPG) {
        super(player.getUniqueId());
        this.mcRPG = mcRPG;
        skillHolder = new SkillHolder(mcRPG, getUUID());
        questHolder = new QuestHolder(getUUID());
        playerSettings = new HashMap<>();
        playerExperienceExtras = new PlayerExperienceExtras();
    }

    public McRPGPlayer(@NotNull UUID uuid, @NotNull McRPG mcRPG) {
        super(uuid);
        this.mcRPG = mcRPG;
        skillHolder = new SkillHolder(mcRPG, getUUID());
        questHolder = new QuestHolder(getUUID());
        playerSettings = new HashMap<>();
        playerExperienceExtras = new PlayerExperienceExtras();
    }

    @Override
    public boolean useMutex() {
        return false;
    }

    /**
     * Gets the {@link McRPG} instance that created this player.
     *
     * @return The {@link McRPG} instance that created this player.
     */
    @NotNull
    public McRPG getMcRPGInstance() {
        return mcRPG;
    }

    /**
     * Gets the {@link SkillHolder} representation of this player, allowing access to McRPG
     * skill functionality.
     *
     * @return The {@link SkillHolder} representation of this player.
     */
    @NotNull
    public SkillHolder asSkillHolder() {
        return skillHolder;
    }

    /**
     * Gets the {@link QuestHolder} representation of this player, allowing access
     * to McRPG quest functionality
     *
     * @return The {@link QuestHolder} representation of this player.
     */
    @NotNull
    public QuestHolder asQuestHolder() {
        return questHolder;
    }

    /**
     * Sets the provided {@link PlayerSetting} as the current setting option for that setting type.
     *
     * @param playerSetting The {@link PlayerSetting} to set.
     */
    public void setPlayerSetting(@NotNull PlayerSetting playerSetting) {
        PlayerSetting oldSetting = playerSettings.get(playerSetting.getSettingKey());
        playerSettings.put(playerSetting.getSettingKey(), playerSetting);
        PlayerSettingChangeEvent playerSettingChangeEvent = new PlayerSettingChangeEvent(this, oldSetting, playerSetting);
        Bukkit.getPluginManager().callEvent(playerSettingChangeEvent);
    }

    /**
     * Gets an {@link Optional} containing the {@link PlayerSetting} that belongs to the provided {@link NamespacedKey},
     *
     * @param key The {@link NamespacedKey} to get the {@link PlayerSetting} for.
     * @return An {@link Optional} containing the {@link PlayerSetting} that belongs to the provided {@link NamespacedKey},
     * or empty if there is not a match.
     */
    @NotNull
    public Optional<PlayerSetting> getPlayerSetting(@NotNull NamespacedKey key) {
        return Optional.ofNullable(playerSettings.get(key));
    }

    /**
     * Gets an {@link ImmutableSet} of all {@link PlayerSetting}s for this player.
     *
     * @return An {@link ImmutableSet} of all {@link PlayerSetting}s for this player.
     */
    @NotNull
    public Set<PlayerSetting> getPlayerSettings() {
        return ImmutableSet.copyOf(playerSettings.values());
    }

    /**
     * Checks to see if this player can start an upgrade quest for the provided {@link TierableAbility}.
     *
     * @param tierableAbility The {@link TierableAbility} to check.
     * @return {@code true} if this player can start an upgrade quest for the provided {@link TierableAbility}
     */
    public boolean canPlayerStartUpgradeQuest(@NotNull TierableAbility tierableAbility) {
        var abilityDataOptional = skillHolder.getAbilityData(tierableAbility);
        if (abilityDataOptional.isPresent()) {
            var tierAttributeOptional = abilityDataOptional.get().getAbilityAttribute(AbilityAttributeManager.ABILITY_TIER_ATTRIBUTE_KEY);
            var questAttributeOptional = abilityDataOptional.get().getAbilityAttribute(AbilityAttributeManager.ABILITY_QUEST_ATTRIBUTE);
            // Validate they don't have an ongoing upgrade quest
            if (skillHolder.hasActiveUpgradeQuest(tierableAbility.getAbilityKey())) {
                return false;
            }
            if (tierAttributeOptional.isPresent() && tierAttributeOptional.get() instanceof AbilityTierAttribute attribute) {
                int currentTier = attribute.getContent();
                int nextTier = currentTier + 1;
                int upgradeCost = tierableAbility.getUpgradeCostForTier(nextTier);
                // If the next tier is below or at the tier cap
                if (tierableAbility.getMaxTier() >= nextTier) {
                    // If the ability has a skill tied to it
                    if (tierableAbility.getSkill().isPresent()) {
                        var skillData = skillHolder.getSkillHolderData(tierableAbility.getSkill().get());
                        // Check if the current skill level is enough to unlock and ensure player has enough upgrade points
                        return skillData.isPresent() && skillData.get().getCurrentLevel() >= tierableAbility.getUnlockLevelForTier(nextTier) && skillHolder.getUpgradePoints() >= upgradeCost;
                    }
                    // If the ability doesn't have a skill, then check if they have enough upgrade points
                    else {
                        return skillHolder.getUpgradePoints() >= upgradeCost;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Starts an upgrade quest for the provided {@link TierableAbility}.
     *
     * @param tierableAbility The {@link TierableAbility} to start an upgrade quest for.
     */
    public void startUpgradeQuest(@NotNull TierableAbility tierableAbility) {
        var abilityDataOptional = skillHolder.getAbilityData(tierableAbility);

        if (abilityDataOptional.isEmpty() || abilityDataOptional.get().getAbilityAttribute(AbilityAttributeManager.ABILITY_QUEST_ATTRIBUTE).isEmpty()
                || abilityDataOptional.get().getAbilityAttribute(AbilityAttributeManager.ABILITY_TIER_ATTRIBUTE_KEY).isEmpty()) {
            throw new IllegalArgumentException("Expected ability quest data for ability " + tierableAbility.getDisplayName());
        }
        int tier = (int) abilityDataOptional.get().getAbilityAttribute(AbilityAttributeManager.ABILITY_TIER_ATTRIBUTE_KEY).get().getContent() + 1;
        Quest quest = tierableAbility.getUpgradeQuestForTier(tier);
        abilityDataOptional.get().addAttribute(new AbilityUpgradeQuestAttribute(quest.getUUID()));
        QuestManager questManager = McRPG.getInstance().getQuestManager();
        skillHolder.setUpgradePoints(skillHolder.getUpgradePoints() - tierableAbility.getUpgradeCostForTier(tier));
        questManager.addActiveQuest(quest);
        questManager.addHolderToQuest(questHolder, quest);
        quest.startQuest();
    }


    /**
     * Gets the {@link PlayerExperienceExtras} for this player.
     *
     * @return The {@link PlayerExperienceExtras} for this player.
     */
    @NotNull
    public PlayerExperienceExtras getExperienceExtras() {
        return playerExperienceExtras;
    }

    /**
     * Saves all player data using the provided {@link Connection}.
     *
     * @param connection The {@link Connection} to use to save data to.
     */
    public void savePlayer(@NotNull Connection connection) {
        BatchTransaction batchTransaction = new BatchTransaction(connection);
        FailSafeTransaction failsafeTransaction = new FailSafeTransaction(connection);
        failsafeTransaction.addAll(SkillDAO.saveAllSkillHolderInformation(connection, skillHolder));
        failsafeTransaction.addAll(LoadoutInfoDAO.saveAllLoadoutInfo(connection, skillHolder));
        failsafeTransaction.addAll(LoadoutAbilityDAO.saveAllLoadouts(connection, skillHolder));
        failsafeTransaction.addAll(LoadoutDisplayDAO.saveAllLoadoutDisplays(connection, skillHolder));
        failsafeTransaction.addAll(PlayerExperienceExtrasDAO.savePlayerExperienceExtras(connection, getUUID(), playerExperienceExtras));
        batchTransaction.addAll(PlayerSettingDAO.savePlayerSettings(connection, getUUID(), getPlayerSettings()));
        failsafeTransaction.executeTransaction();
        batchTransaction.executeTransaction();
    }
}
