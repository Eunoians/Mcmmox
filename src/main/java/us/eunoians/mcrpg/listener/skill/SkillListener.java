package us.eunoians.mcrpg.listener.skill;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.configuration.FileType;
import us.eunoians.mcrpg.configuration.file.MainConfigFile;
import us.eunoians.mcrpg.entity.EntityManager;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.skill.Skill;
import us.eunoians.mcrpg.skill.SkillRegistry;
import us.eunoians.mcrpg.skill.experience.context.SkillExperienceContext;

import java.util.Optional;
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
                                var eventContextOptional = getEventContext(skillHolder, skill, exp, event);
                                if (eventContextOptional.isPresent()) {
                                    double modifier = Math.min(mcRPG.getFileManager().getFile(FileType.MAIN_CONFIG).getDouble(MainConfigFile.EXPERIENCE_MULTIPLIER_LIMIT), mcRPG.getExperienceModifierRegistry().calculateModifierForContext(eventContextOptional.get()));
                                    exp = (int) (exp * modifier);
                                }
                                skillHolderData.addExperience(exp);
                            }
                        }));
            }
        });
    }

    /**
     * Get an {@link Optional} containing {@link SkillExperienceContext} for an {@link Event} that this listener handles.
     *
     * @param skillHolder The {@link SkillHolder} who the context is about.
     * @param skill       The {@link Skill} gaining experience in this context.
     * @param experience  The base amount of experience being given in this context.
     * @param event       The {@link Event} that is creating the context.
     * @return An {@link Optional} containing {@link SkillExperienceContext} for an {@link Event} that this listener handles.
     */
    @NotNull
    Optional<SkillExperienceContext<?>> getEventContext(@NotNull SkillHolder skillHolder, @NotNull Skill skill, int experience, @NotNull Event event);
}
