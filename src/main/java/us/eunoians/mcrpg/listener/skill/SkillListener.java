package us.eunoians.mcrpg.listener.skill;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.EntityManager;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.skill.SkillRegistry;

import java.util.Set;
import java.util.UUID;

/**
 * A generic listener that provides base implementation for any {@link Event} that
 * can be used to level up a {@link us.eunoians.mcrpg.skill.Skill}.
 */
public interface SkillListener extends Listener {

    /**
     * Attempts to pass in the provided {@link Event} to all {@link us.eunoians.mcrpg.skill.Skill}s owned
     * by the provided {@link UUID} to be parsed for leveling.
     *
     * @param uuid  The {@link UUID} of the {@link SkillHolder} to attempt to level skills for.
     * @param event The {@link Event} being passed in for leveling.
     */
    default void levelSkill(@NotNull UUID uuid, @NotNull Event event) {
        McRPG mcRPG = McRPG.getInstance();
        EntityManager entityManager = mcRPG.getEntityManager();
        SkillRegistry skillRegistry = mcRPG.getSkillRegistry();
        entityManager.getAbilityHolder(uuid).ifPresent(abilityHolder -> {

            // Validate that the holder specific context allows for McRPG to be used here.
            if (!mcRPG.getWorldManager().isMcRPGEnabledForHolder(abilityHolder)) {
                return;
            }

            if (abilityHolder instanceof SkillHolder skillHolder) {
                Set<NamespacedKey> allSkills = skillHolder.getSkills();

                allSkills.stream().map(skillRegistry::getRegisteredSkill)
                        .filter(skill -> skill.canEventLevelSkill(event))
                        .forEach(skill -> skillHolder.getSkillHolderData(skill).ifPresent(skillHolderData -> {
                            int exp = skill.calculateExperienceToGive(skillHolder, event);
                            if (exp > 0) {
                                skillHolderData.addExperience(exp);
                            }
                        }));
            }
        });
    }
}
