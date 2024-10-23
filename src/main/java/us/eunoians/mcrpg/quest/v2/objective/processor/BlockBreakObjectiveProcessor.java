package us.eunoians.mcrpg.quest.v2.objective.processor;

import com.google.common.collect.ImmutableSet;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.quest.v2.objective.QuestObjectiveV2;
import us.eunoians.mcrpg.util.McRPGMethods;
import us.eunoians.mcrpg.world.WorldManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockBreakObjectiveProcessor extends QuestObjectiveProcessor {

    public static final NamespacedKey PROCESSOR_KEY = new NamespacedKey(McRPGMethods.getMcRPGNamespace(), "block-break-processor");

    private final Set<Material> allowedBlocks;
    private final Set<Material> bannedBlocks;

    public BlockBreakObjectiveProcessor() {
        allowedBlocks = new HashSet<>();
        bannedBlocks = new HashSet<>();
    }

    /**
     * Adds the provided {@link Material}s to the allow list.
     *
     * @param material The {@link Material}s to add to the allow list
     */
    public void addAllowedBlocks(@NotNull Material... material) {
        allowedBlocks.addAll(List.of(material));
    }

    /**
     * Adds the provided {@link Material}s to the ban list.
     *
     * @param material The {@link Material}s to add to the ban list.
     */
    public void addBannedBlocks(@NotNull Material... material) {
        bannedBlocks.addAll(List.of(material));
    }

    /**
     * Gets a copy of the {@link Set} of all allowed blocks.
     *
     * @return A copy of the {@link Set} of all allowed blocks.
     */
    @NotNull
    public Set<Material> getAllowedBlocks() {
        return ImmutableSet.copyOf(allowedBlocks);
    }

    /**
     * Gets a copy of the {@link Set} of all banned blocks.
     *
     * @return A copy of the {@link Set} of all banned blocks.
     */
    @NotNull
    public Set<Material> getBannedBlocks() {
        return ImmutableSet.copyOf(bannedBlocks);
    }

    @NotNull
    @Override
    public NamespacedKey getProcessorKey() {
        return PROCESSOR_KEY;
    }

    @Override
    public boolean canProcessEvent(@NotNull QuestObjectiveV2 questObjectiveV2, @NotNull Event event) {
        AllowMode allowMode = AllowMode.getAllowMode(this);
        return event instanceof BlockBreakEvent blockBreakEvent &&
                WorldManager.isBlockNatural(blockBreakEvent.getBlock()) &&
                !blockBreakEvent.isCancelled() &&
                !questObjectiveV2.isObjectiveComplete() &&
//                questHolder.getUUID().equals(blockBreakEvent.getPlayer().getUniqueId()) &&
//                questHolder.isQuestActive(getQuest()) &&
                allowMode.isMaterialAllowed(this, blockBreakEvent.getBlock().getType()) &&
                blockBreakEvent.getPlayer().getGameMode() == GameMode.SURVIVAL;
    }

    @Override
    public void processEvent(@NotNull QuestObjectiveV2 questObjective, @NotNull Event event) {

    }

    @Override
    public void registerProcessor() {

    }

    @Override
    public void unregisterProcessor() {

    }

    /**
     * An enum that represents the mode for validating what {@link Material}s can progress the objective.
     */
    private enum AllowMode {
        ALL,
        ALLOWED,
        BANNED;

        /**
         * Checks to see if the provided {@link Material} can progress the objective based on the allow or ban settings
         *
         * @param blockBreakObjectiveProcessor The {@link BlockBreakObjectiveProcessor} to check the lists of
         * @param material                 The {@link Material} to check
         * @return {@code true} if the provided {@link Material} is valid for this allow mode.
         */
        public boolean isMaterialAllowed(@NotNull BlockBreakObjectiveProcessor blockBreakObjectiveProcessor, @NotNull Material material) {
            return switch (this) {
                case ALLOWED -> blockBreakObjectiveProcessor.getAllowedBlocks().contains(material);
                case BANNED -> !blockBreakObjectiveProcessor.getBannedBlocks().contains(material);
                case ALL -> true;
            };
        }

        /**
         * Gets the allow mode for the given {@link BlockBreakObjectiveProcessor} based on the state of the allow/ban list.
         *
         * @param blockBreakObjectiveProcessor The {@link BlockBreakObjectiveProcessor} to get the allow mode for.
         * @return If both {@link BlockBreakObjectiveProcessor#getAllowedBlocks()} and {@link BlockBreakObjectiveProcessor#getBannedBlocks()}
         * return an empty {@link Set}, then {@link AllowMode#ALL} is returned. If both the allow and ban list are non-empty, then {@link AllowMode#ALLOWED}
         * is returned. Otherwise, it will return the allow mode corresponding to the list that has contents.
         */
        public static AllowMode getAllowMode(@NotNull BlockBreakObjectiveProcessor blockBreakObjectiveProcessor) {
            if (blockBreakObjectiveProcessor.getAllowedBlocks().isEmpty()) {
                if (blockBreakObjectiveProcessor.getBannedBlocks().isEmpty()) {
                    return ALL;
                } else {
                    return BANNED;
                }
            } else {
                return ALLOWED;
            }
        }
    }
}
