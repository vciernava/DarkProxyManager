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

public class PendingLobbyServers {
  private static List<UUID> list = new ArrayList<>();

  public static void add(UUID uuid, DarkProxyManager DarkProxyManager) throws IOException, ApiException {
    list.add(uuid);
    registerServer(DarkProxyManager.getProxy(), uuid);
  }

  public static void registerServer(ProxyServer proxy, UUID uuid) throws IOException, ApiException {
    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);
    CoreV1Api api = new CoreV1Api();

    V1Pod server = api.readNamespacedPod("lobby." + uuid, "default", null);

    String serverIP = server.getStatus().getPodIP();
    int serverPort = server.getSpec().getContainers().get(0).getPorts().get(0).getContainerPort();

    ServerInfo serverInfo = new ServerInfo("lobby." + (list.indexOf(uuid)+1), new InetSocketAddress(InetAddress.getByName(serverIP), serverPort));

    proxy.registerServer(serverInfo);
  }

  public static List<UUID> getList() {
    return list;
  }

  public static UUID getList(int index) {
    return list.get(index);
  }
}
