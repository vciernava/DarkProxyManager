package cz.darklabs.darkproxymanager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import lombok.Getter;

public class PendingLobbyServers {
    @Getter
    private static final List<UUID> lobbies = new ArrayList<>();

    public static void add(UUID uuid, DarkProxyManager DarkProxyManager) throws IOException, ApiException {
        lobbies.add(uuid);
        registerServer(DarkProxyManager.getProxy(), uuid);
    }

    public static void registerServer(ProxyServer proxy, UUID uuid) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        V1Pod server = api.readNamespacedPod("lobby." + uuid, "default", null);
        if (server.getStatus() == null || server.getSpec() == null) {
            throw new RuntimeException("Server not found!");
        }

        String serverIP = server.getStatus().getPodIP();
        int serverPort = server.getSpec().getContainers().get(0).getPorts().get(0).getContainerPort();

        ServerInfo serverInfo = new ServerInfo("lobby.1", new InetSocketAddress(InetAddress.getByName(serverIP), serverPort));

        proxy.registerServer(serverInfo);
    }
}
