package us.eunoians.mcrpg.integration.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;
import us.eunoians.mcrpg.skill.SkillRegistry;

public class McRPGGSkillLevelCondition extends Condition {

    private final McRPG plugin;
    private final NamespacedKey skillKey;
    private final VariableNumber level;

    public McRPGGSkillLevelCondition(@NotNull Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.plugin = McRPG.getInstance();
        this.skillKey = new NamespacedKey(plugin, instruction.next());
        SkillRegistry skillRegistry = plugin.getSkillRegistry();
        if (!skillRegistry.isSkillRegistered(skillKey)) {
            throw new InstructionParseException("Skill '" + skillKey + "' is not registered.");
        }
        level = instruction.getVarNum();

    }

    @Override
    protected Boolean execute(Profile profile) throws QuestRuntimeException {
        var playerOptional = plugin.getPlayerManager().getPlayer(profile.getPlayerUUID());
        if (playerOptional.isPresent() && playerOptional.get() instanceof McRPGPlayer mcRPGPlayer) {
            SkillHolder skillHolder = mcRPGPlayer.asSkillHolder();
            var skillData = skillHolder.getSkillHolderData(skillKey);
            if (skillData.isPresent()) {
                return skillData.get().getCurrentLevel() >= level.getValue(profile).intValue();
            }
        }
        throw new QuestRuntimeException("Player " + profile.getPlayerUUID() + " is not online.");
    }
}
