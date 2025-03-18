package us.eunoians.mcrpg.external.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;

/**
 * A hook for containing all code related to <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI</a>
 * that McRPG needs in order to support it.
 */
public class PapiHook {

    private final McRPG plugin;

    public PapiHook(McRPG plugin) {
        this.plugin = plugin;
    }

    /**
     * Translates the provided message using the provided {@link OfflinePlayer} to use
     * for placeholders.
     *
     * @param player  The {@link OfflinePlayer} to use for placeholders.
     * @param message The message that needs placeholders replaced.
     * @return The message with placeholders replaced.
     */
    @NotNull
    public String translateMessage(@NotNull OfflinePlayer player, @NotNull String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
