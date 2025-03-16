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
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.entity.player.PlayerExperienceExtras;
import us.eunoians.mcrpg.skill.experience.context.SkillExperienceContext;

import java.util.UUID;

/**
 * This experience modifier consumes a player's "boosted experience" and gives them
 * bonus experience until they run out.
 */
public final class BoostedExperienceModifier extends ExperienceModifier {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(McRPG.getInstance(), "boosted-experience-modifier");

    private final McRPG mcRPG;

    public BoostedExperienceModifier(@NotNull McRPG mcRPG) {
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
            return playerExperienceExtras.getBoostedExperience() > 0;
        }
        return false;
    }

    @Override
    public double getModifier(@NotNull SkillExperienceContext<? extends Event> skillExperienceContext) {
        UUID uuid = skillExperienceContext.getSkillHolder().getUUID();
        var playerOptional = mcRPG.getPlayerManager().getPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (playerOptional.isPresent() && playerOptional.get() instanceof McRPGPlayer mcRPGPlayer && player != null) {
            PlayerExperienceExtras playerExperienceExtras = mcRPGPlayer.getExperienceExtras();
            int playerBoostedExperience = playerExperienceExtras.getBoostedExperience();
            try {
                double boostToApply = mcRPG.getFileManager().getFile(FileType.MAIN_CONFIG).getDouble(MainConfigFile.BOOSTED_EXPERIENCE_USAGE_RATE);
                double boostedExperience = skillExperienceContext.getBaseExperience() * boostToApply;
                // If the end result is larger than the amount of experience we can give, then we need to find the multiplier of base exp that gets us to the remaining exp
                if (boostedExperience > playerBoostedExperience) {
                    boostToApply = (double) playerBoostedExperience / skillExperienceContext.getBaseExperience();
                    boostedExperience = playerBoostedExperience;
                }
                // Consume boosted experience
                playerExperienceExtras.modifyRedeemableExperience((int) (boostedExperience * -1));
                // Normalize the boost
                return boostToApply - 1;
            }
            catch (ParseError | EvaluationException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
