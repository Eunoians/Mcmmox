package us.eunoians.mcrpg.integration.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BetonQuestUtils {

    @NotNull
    public static Profile getProfile(@NotNull Player player) {
        return PlayerConverter.getID(player);
    }

    public boolean doesPlayerHaveTag(@NotNull Player player, @NotNull String tag) {
        return BetonQuest.getInstance().getPlayerData(getProfile(player)).hasTag(tag);
    }
}
