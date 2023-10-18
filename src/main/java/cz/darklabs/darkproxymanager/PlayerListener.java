package cz.darklabs.darkproxymanager;

import java.io.IOException;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import io.kubernetes.client.openapi.ApiException;

public class PlayerListener {

    private final ProxyServer proxy;
    private DarkProxyManager DarkProxyManager;

    public PlayerListener(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Subscribe()
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        try {
            if (proxy.getServer("lobby." + 1).isPresent()) {
                event.setInitialServer(proxy.getServer("lobby." + 1).get());
            } else {
                ServerManager.createLobbyServer(DarkProxyManager);
            }
        } catch (Exception e) {
            DarkProxyManager.getLogger().error(LoggerColors.RED_BOLD + "Error while connecting player to lobby server!" + "\u001B[0m");
        }
    }
}
