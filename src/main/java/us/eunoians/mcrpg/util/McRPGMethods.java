package us.eunoians.mcrpg.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;

public class McRPGMethods {

    private static final String MCRPG_NAMESPACED_KEY = "mcrpg";

    @NotNull
    public static String getMcRPGNamespace() {
        return MCRPG_NAMESPACED_KEY;
    }

    @NotNull
    public static Component translate(@NotNull String message) {
        return McRPG.getInstance().getMiniMessage().deserialize(message);
    }
}
