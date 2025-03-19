package us.eunoians.mcrpg.ability.impl;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.route.Route;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;

/**
 * This interface represents an {@link Ability} that has configuration that comes out of
 * a config file.
 */
public interface ConfigurableAbility extends Ability {

    /**
     * Gets the {@link YamlDocument} used to pull configuration data out of.
     *
     * @return The {@link YamlDocument} used to pull configuration data out of.
     */
    @NotNull
    YamlDocument getYamlDocument();

    @NotNull
    @Override
    default String getDisplayName(@NotNull McRPGPlayer player) {
        return player.getMcRPGInstance().getLocalizationManager().getLocalizedMessage(player, getDisplayNameRoute())
    }

    /**
     * Gets the {@link Route} to pull the ability's display name from.
     *
     * @return The {@link Route} to pull the ability's display name from.
     */
    @NotNull
    Route getDisplayNameRoute();
}
