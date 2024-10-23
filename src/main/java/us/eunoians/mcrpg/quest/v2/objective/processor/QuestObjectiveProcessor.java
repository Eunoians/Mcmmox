package us.eunoians.mcrpg.quest.v2.objective.processor;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.quest.v2.objective.QuestObjectiveV2;

public abstract class QuestObjectiveProcessor implements Listener {

    @NotNull
    public abstract NamespacedKey getProcessorKey();

    public abstract boolean canProcessEvent(@NotNull QuestObjectiveV2 questObjectiveV2, @NotNull Event event);

    public abstract void processEvent(@NotNull QuestObjectiveV2 questObjective, @NotNull Event event);

    public abstract void registerProcessor();

    public abstract void unregisterProcessor();
}
