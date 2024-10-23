package us.eunoians.mcrpg.quest.v2;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.quest.QuestReward;
import us.eunoians.mcrpg.quest.objective.QuestObjective;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class QuestV2 {

    private final UUID questUUID;
    private final QuestFrame questFrame;
    private final Set<QuestObjective> questObjectives;
    private boolean started = false;
    private boolean abandoned = false;
    private boolean completed = false;
    private QuestReward questReward;

    public QuestV2(@NotNull QuestFrame questFrame) {
        this.questUUID = UUID.randomUUID();
        this.questFrame = questFrame;
        this.questObjectives = new HashSet<>();
    }

    public QuestV2(@NotNull UUID questUUID, @NotNull QuestFrame questFrame) {
        this.questUUID = questUUID;
        this.questFrame = questFrame;
        this.questObjectives = new HashSet<>();
    }

    protected Set<QuestObjective> createQuestObjectives(@NotNull Set<NamespacedKey> objectiveKeys) {
        Set<QuestObjective> questObjectives = new HashSet<>();

        for (NamespacedKey key : objectiveKeys) {

        }
        return questObjectives;
    }
}
