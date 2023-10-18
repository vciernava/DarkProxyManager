package cz.darklabs.darkproxymanager.Configs;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import cz.darklabs.darkproxymanager.DarkProxyManager;
import lombok.Getter;
import lombok.Setter;

public class Configurations {
    @Getter
    private static Config config;
    private static Path configFile;

    /**
     * Loads the config files.
     *
     * @param DarkProxyManager
     */
    public static void loadConfigs(DarkProxyManager DarkProxyManager) {
        configFile = Path.of(DarkProxyManager.getDataDirectory() + "/config.toml");

        //Create data directory
        if (!DarkProxyManager.getDataDirectory().toFile().exists()) {
            DarkProxyManager.getDataDirectory().toFile().mkdir();
        }

        //Load the config.toml to memory
        if (!configFile.toFile().exists()) {
            try (InputStream in = DarkProxyManager.class.getResourceAsStream("/config.toml")) {
                assert in != null;
                Files.copy(in, configFile);
            } catch (Exception e) {
                DarkProxyManager.getLogger().error("Error loading config.toml");
            }
        }
        config = new Toml().read(configFile.toFile()).to(Config.class);
    }

    /**
     * Save the config
     *
     * @param DarkProxyManager
     */
    public static void saveConfig(DarkProxyManager DarkProxyManager) {
        try {
            new TomlWriter().write(config, configFile.toFile());
        } catch (Exception e) {
            DarkProxyManager.getLogger().error("Error saving config.toml");
        }
    }

    /**
     * The main config
     */
    public class Config {

        @Getter
        @Setter
        private int lobbies;
        @Getter
        @Setter
        private String lobbyImage;

        @Override
        public String toString() {
            return "Panel{" +
                    "lobbies='" + lobbies + '\'' +
                    ", lobbyImage='" + lobbyImage + '\'' +
                    '}';
        }
    }
}
