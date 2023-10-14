package cz.darklabs.darkproxymanager;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;
import java.nio.file.Path;
import lombok.Getter;

import org.slf4j.Logger;

@Plugin(
        id = "darkproxymanager",
        name = "DarkProxyManager",
        version = "1.0-SNAPSHOT",
        authors = "Darklabs Team"
)
public class DarkProxyManager {

    @Getter private final Logger logger;
    @Getter private final ProxyServer proxy;
    @Getter private final Path dataDirectory;

    @Inject
    public DarkProxyManager(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new PlayerListener(proxy));
        proxy.getCommandManager().register("lobbies", new GetLobbies(this));

        Configs.loadConfigs(this);

        startLobbyServers();
    }

    private void startLobbyServers() {
        if(Configs.getConfig().getLobbies() == 0) {
            logger.error(LoggerColors.RED_BOLD + "No lobbies have been started, you can change this in config file!" + "\u001B[0m");
            return;
        }

        logger.info(LoggerColors.CYAN_BOLD + "Starting lobby servers..." + "\u001B[0m");

        try {
            for(int i = 0; i < Configs.getConfig().getLobbies(); i++) {
                ServerManager.createLobbyServer(this);
            }

            logger.info(LoggerColors.GREEN_BOLD + "Lobby servers started successfully!" + "\u001B[0m");
        } catch (ApiException | IOException e) {
            e.printStackTrace();
        }
    }
}