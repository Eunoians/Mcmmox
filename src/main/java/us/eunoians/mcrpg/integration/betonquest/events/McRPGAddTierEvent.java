package us.eunoians.mcrpg.integration.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.NamespacedKey;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.ability.AbilityData;
import us.eunoians.mcrpg.ability.AbilityRegistry;
import us.eunoians.mcrpg.ability.attribute.AbilityAttributeManager;
import us.eunoians.mcrpg.ability.attribute.AbilityTierAttribute;
import us.eunoians.mcrpg.ability.impl.Ability;
import us.eunoians.mcrpg.ability.impl.TierableAbility;
import us.eunoians.mcrpg.entity.holder.SkillHolder;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;

public class McRPGAddTierEvent extends QuestEvent {

    private final McRPG plugin;
    private final NamespacedKey abilityKey;
    private final VariableNumber tiers;

    public McRPGAddTierEvent(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.plugin = McRPG.getInstance();
        this.abilityKey = new NamespacedKey(plugin, instruction.next());
        AbilityRegistry abilityRegistry = plugin.getAbilityRegistry();
        if (!abilityRegistry.isAbilityRegistered(abilityKey)) {
            throw new InstructionParseException("Ability '" + abilityKey + "' is not registered.");
        }
        tiers = instruction.getVarNum();
    }

    @Override
    protected Void execute(Profile profile) throws QuestRuntimeException {
        var playerOptional = plugin.getPlayerManager().getPlayer(profile.getPlayerUUID());
        if (playerOptional.isPresent() && playerOptional.get() instanceof McRPGPlayer mcRPGPlayer) {
            SkillHolder skillHolder = mcRPGPlayer.asSkillHolder();
            Ability ability = plugin.getAbilityRegistry().getRegisteredAbility(abilityKey);
            var abilityDataOptional = skillHolder.getAbilityData(abilityKey);
            if (ability instanceof TierableAbility tierableAbility && abilityDataOptional.isPresent()) {
                AbilityData abilityData = abilityDataOptional.get();
                var attribute = abilityData.getAbilityAttribute(AbilityAttributeManager.ABILITY_TIER_ATTRIBUTE_KEY);
                if (attribute.isPresent() && attribute.get() instanceof AbilityTierAttribute abilityTierAttribute) {
                    abilityData.updateAttribute(abilityTierAttribute, Math.min(tierableAbility.getMaxTier(), abilityTierAttribute.getContent() + tiers.getValue(profile).intValue()));
                }
            }
        }
        throw new QuestRuntimeException("Player " + profile.getPlayerUUID() + " is not online.");
    }
}
