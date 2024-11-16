package us.eunoians.mcrpg.integration.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
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

public class McRPGAddLevelEvent extends QuestEvent {

    private final McRPG plugin;
    private final NamespacedKey skillKey;
    private final VariableNumber level;

    public McRPGAddLevelEvent(@NotNull Instruction instruction) throws InstructionParseException {
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
    protected Void execute(@NotNull Profile profile) throws QuestRuntimeException {
        var playerOptional = plugin.getPlayerManager().getPlayer(profile.getPlayerUUID());
        if (playerOptional.isPresent() && playerOptional.get() instanceof McRPGPlayer mcRPGPlayer) {
            SkillHolder skillHolder = mcRPGPlayer.asSkillHolder();
            var skillData = skillHolder.getSkillHolderData(skillKey);
            if (skillData.isPresent()) {
                skillData.get().addLevel(level.getValue(profile).intValue());
            }
        }
        throw new QuestRuntimeException("Player " + profile.getPlayerUUID() + " is not online.");
    }
}
