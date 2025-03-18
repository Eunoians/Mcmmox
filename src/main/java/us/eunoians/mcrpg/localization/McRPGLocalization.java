package us.eunoians.mcrpg.localization;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.expansion.content.McRPGContent;

import java.util.Locale;

/**
 * A localization that provides a configuration file in the form of a {@link YamlDocument}
 * for a specific {@link Locale}.
 */
public interface McRPGLocalization extends McRPGContent {

    /**
     * Gets the {@link Locale} supported by this localization.
     * @return The {@link Locale} supported by this localization.
     */
    @NotNull
    Locale getLocale();

    /**
     * Gets the {@link YamlDocument} containing the configuration for this
     * localization.
     * @return The {@link YamlDocument} containing the configuration for this
     * localization.
     */
    @NotNull
    YamlDocument getConfigurationFile();

}
