package us.eunoians.mcrpg.api.event.skill;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class SkillEvent extends Event {

    private final NamespacedKey skillKey;

    public SkillEvent(@NotNull NamespacedKey skillKey) {
        this.skillKey = skillKey;
    }

    @NotNull
    public NamespacedKey getSkillKey() {
        return skillKey;
    }
}
