package us.eunoians.mcrpg.integration.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.integration.betonquest.conditions.McRPGGSkillLevelCondition;
import us.eunoians.mcrpg.integration.betonquest.events.McRPGAddExpEvent;
import us.eunoians.mcrpg.integration.betonquest.events.McRPGAddLevelEvent;
import us.eunoians.mcrpg.integration.betonquest.events.McRPGAddTierEvent;

public class BetonQuestIntegration {

    private final McRPG mcRPG;

    public BetonQuestIntegration(@NotNull McRPG mcRPG) {
        this.mcRPG = mcRPG;
        setupIntegration();
    }

    public void setupIntegration() {
        if (mcRPG.isBetonQuestEnabled()) {
            BetonQuest betonQuest = (BetonQuest) Bukkit.getPluginManager().getPlugin("BetonQuest");
            // Conditions
            betonQuest.registerConditions("mcrpglevel", McRPGGSkillLevelCondition.class);

            // Events
            betonQuest.registerEvents("mcrpgaddexp", McRPGAddExpEvent.class);
            betonQuest.registerEvents("mcrpgaddlevel", McRPGAddLevelEvent.class);
            betonQuest.registerEvents("mcrpgaddtier", McRPGAddTierEvent.class);
        }
    }
}
