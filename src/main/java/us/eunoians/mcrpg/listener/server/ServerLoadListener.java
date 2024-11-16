package us.eunoians.mcrpg.listener.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public class ServerLoadListener implements Listener {

    private final ServerLoadFunction serverLoadFunction;

    public ServerLoadListener(@NotNull ServerLoadFunction serverLoadFunction) {
        this.serverLoadFunction = serverLoadFunction;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerLoad(@NotNull ServerLoadEvent event) {
        serverLoadFunction.onLoad();
    }
}
