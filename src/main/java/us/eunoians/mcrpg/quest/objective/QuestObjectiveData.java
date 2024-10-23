package us.eunoians.mcrpg.quest.objective;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class QuestObjectiveData {

    private final UUID questUUID;
    private final NamespacedKey objectiveKey;

    public QuestObjectiveData(@NotNull UUID questUUID, @NotNull NamespacedKey objectiveKey) {
        this.questUUID = questUUID;
        this.objectiveKey = objectiveKey;
    }
}
