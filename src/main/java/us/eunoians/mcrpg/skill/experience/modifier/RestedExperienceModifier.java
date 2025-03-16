package us.eunoians.mcrpg.skill.experience.modifier;

import com.diamonddagger590.mccore.parser.EvaluationException;
import com.diamonddagger590.mccore.parser.ParseError;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.configuration.FileType;
import us.eunoians.mcrpg.configuration.file.MainConfigFile;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.entity.player.PlayerExperienceExtras;
import us.eunoians.mcrpg.skill.experience.context.SkillExperienceContext;

import java.util.UUID;

/**
 * This modifier will consume a player's rested experience to modify their experience gain.
 */
public class RestedExperienceModifier extends ExperienceModifier {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(McRPG.getInstance(), "rested-experience-modifier");

    private final McRPG mcRPG;

    public RestedExperienceModifier(@NotNull McRPG mcRPG) {
        this.mcRPG = mcRPG;
    }

    @Override
    public NamespacedKey getModifierKey() {
        return MODIFIER_KEY;
    }

    @Override
    public boolean canProcessContext(@NotNull SkillExperienceContext<? extends Event> skillExperienceContext) {
        var playerOptional = mcRPG.getPlayerManager().getPlayer(skillExperienceContext.getSkillHolder().getUUID());
        if (playerOptional.isPresent() && playerOptional.get() instanceof McRPGPlayer mcRPGPlayer) {
            PlayerExperienceExtras playerExperienceExtras = mcRPGPlayer.getExperienceExtras();
            return playerExperienceExtras.getRestedExperience() > 0;
        }
        return false;
    }

    @Override
    public double getModifier(@NotNull SkillExperienceContext<? extends Event> skillExperienceContext) {
        UUID uuid = skillExperienceContext.getSkillHolder().getUUID();
        var playerOptional = mcRPG.getPlayerManager().getPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (playerOptional.isPresent() && playerOptional.get() instanceof McRPGPlayer mcRPGPlayer && player != null) {
            var skillHolderDataOptional = mcRPGPlayer.asSkillHolder().getSkillHolderData(skillExperienceContext.getSkill());
            if (skillHolderDataOptional.isPresent()) {
                SkillHolder.SkillHolderData skillHolderData = skillHolderDataOptional.get();
                PlayerExperienceExtras playerExperienceExtras = mcRPGPlayer.getExperienceExtras();
                int experienceForNextLevel = skillHolderData.getExperienceForNextLevel();
                double playerRestedExperience = playerExperienceExtras.getRestedExperience();
                try {
                    double boostToApply = mcRPG.getFileManager().getFile(FileType.MAIN_CONFIG).getDouble(MainConfigFile.RESTED_EXPERIENCE_USAGE_RATE);
                    double boostedExperience = skillExperienceContext.getBaseExperience() * boostToApply;
                    double consumedRestedExperience = boostedExperience / experienceForNextLevel;
                    // Since rested experience scales based on level, we want to make sure that if we have .5 levels of experience then we don't end up adding more than 50% of the current level's required experience
                    if (consumedRestedExperience > playerRestedExperience) {
                        // (1000 * .5)/experience
                        boostToApply = (experienceForNextLevel * playerRestedExperience) / skillExperienceContext.getBaseExperience();
                        consumedRestedExperience = playerRestedExperience;
                    }
                    playerExperienceExtras.modifyRestedExperience((float) (consumedRestedExperience * -1));
                    // Normalize the boost
                    return boostToApply - 1;
                } catch (ParseError | EvaluationException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
