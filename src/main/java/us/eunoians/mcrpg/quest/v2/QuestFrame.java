package us.eunoians.mcrpg.quest.v2;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.quest.QuestReward;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class QuestFrame {

    private final McRPG plugin;
    private final NamespacedKey frameKey;
    private final Set<NamespacedKey> objectiveKeys;
    private QuestReward questReward;

    public QuestFrame(@NotNull McRPG mcRPG, @NotNull NamespacedKey frameKey) {
        this.plugin = mcRPG;
        this.frameKey = frameKey;
        this.objectiveKeys = new HashSet<>();
    }

    public QuestFrame(@NotNull McRPG mcRPG, @NotNull NamespacedKey frameKey, @NotNull Set<NamespacedKey> objectiveKeys) {
        this.plugin = mcRPG;
        this.frameKey = frameKey;
        this.objectiveKeys = objectiveKeys;
    }

    public QuestFrame(@NotNull McRPG mcRPG, @NotNull NamespacedKey frameKey, @NotNull Set<NamespacedKey> objectiveKeys, @NotNull QuestReward questReward) {
        this.plugin = mcRPG;
        this.frameKey = frameKey;
        this.objectiveKeys = objectiveKeys;
        this.questReward = questReward;
    }

    @NotNull
    public McRPG getPlugin() {
        return plugin;
    }

    @NotNull
    public NamespacedKey getFrameKey() {
        return frameKey;
    }

    @NotNull
    public Set<NamespacedKey> getObjectiveKeys() {
        return ImmutableSet.copyOf(objectiveKeys);
    }

    @NotNull
    public Optional<QuestReward> getQuestReward() {
        return Optional.ofNullable(questReward);
    }

    public void addObjectiveKey(@NotNull NamespacedKey objectiveKey) {
        objectiveKeys.add(objectiveKey);
    }

    public boolean hasObjectiveKey(@NotNull NamespacedKey objectiveKey) {
        return objectiveKeys.contains(objectiveKey);
    }

    public void removeObjectiveKey(@NotNull NamespacedKey objectiveKey) {
        objectiveKeys.remove(objectiveKey);
    }

    @NotNull
    public QuestV2 createNewQuestFromFrame() {
        return new QuestV2(this);
    }
}
