package us.eunoians.mcrpg.quest.v2;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.expansion.content.McRPGContent;
import us.eunoians.mcrpg.quest.v2.objective.QuestObjectiveFrame;
import us.eunoians.mcrpg.quest.v2.objective.processor.QuestObjectiveProcessor;

import java.util.HashSet;
import java.util.Set;

public abstract class QuestBundle implements McRPGContent {

    private final Set<QuestObjectiveProcessor> questObjectiveProcessors;
    private final Set<QuestObjectiveFrame> questObjectiveFrames;
    private final Set<QuestFrame> questFrames;

    public QuestBundle() {
        questObjectiveProcessors = new HashSet<>();
        questObjectiveFrames = new HashSet<>();
        questFrames = new HashSet<>();
    }

    public QuestBundle(@NotNull Set<QuestObjectiveProcessor> questObjectiveProcessors, @NotNull Set<QuestObjectiveFrame> questObjectiveFrames, @NotNull Set<QuestFrame> questFrames) {
        this.questObjectiveProcessors = questObjectiveProcessors;
        this.questObjectiveFrames = questObjectiveFrames;
        this.questFrames = questFrames;
    }

    public void addQuestObjectiveProcessor(@NotNull QuestObjectiveProcessor questObjectiveProcessor) {
        questObjectiveProcessors.add(questObjectiveProcessor);
    }

    public void addQuestObjectiveFrame(@NotNull QuestObjectiveFrame questObjectiveFrame) {
        questObjectiveFrames.add(questObjectiveFrame);
    }

    public void addQuestFrame(@NotNull QuestFrame questFrame) {
        questFrames.add(questFrame);
    }

    @NotNull
    public Set<QuestObjectiveProcessor> getQuestObjectiveProcessors() {
        return ImmutableSet.copyOf(questObjectiveProcessors);
    }

    @NotNull
    public Set<QuestObjectiveFrame> getQuestObjectiveFrames() {
        return ImmutableSet.copyOf(questObjectiveFrames);
    }

    @NotNull
    public Set<QuestFrame> getQuestFrames() {
        return ImmutableSet.copyOf(questFrames);
    }
}
