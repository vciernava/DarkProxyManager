package cz.darklabs.darkproxymanager;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1ObjectFieldSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.util.Config;

public class ServerManager {

  private static int startingPort = 30000;
  private static int endingPort = 31000;

  private static List<Integer> usedPorts = new ArrayList<>();

  private static int generateUniquePort() {
      for (int port = startingPort; port <= endingPort; port++) {
          if (isPortAvailable(port)) {
              usedPorts.add(port);
              return port;
          }
      }
      throw new RuntimeException("No available ports in the specified range.");
  }

  private static boolean isPortAvailable(int port) {
      if (usedPorts.contains(port)) {
          return false;
      }
      try (Socket ignored = new Socket("localhost", port)) {
          return false;
      } catch (IOException ignored) {
          return true;
      }
  } 

  public static void createLobbyServer(DarkProxyManager DarkProxyManager) throws IOException, ApiException {
    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);
    CoreV1Api api = new CoreV1Api();

    UUID uuid = UUID.randomUUID();
    int port = generateUniquePort();

    Map<String, String> labels = new HashMap<>();
    List<V1Container> containers = new ArrayList<>();
    List<V1EnvVar> envVars = new ArrayList<>();

    labels.put("app", "LobbyServer");
    envVars.add(new V1EnvVar().name("POD_NAME").valueFrom(new V1EnvVarSource().fieldRef(new V1ObjectFieldSelector().fieldPath("metadata.name"))));
    envVars.add(new V1EnvVar().name("POD_IP").valueFrom(new V1EnvVarSource().fieldRef(new V1ObjectFieldSelector().fieldPath("status.podIP"))));
    envVars.add(new V1EnvVar().name("POD_PORT").value(port + ""));

    V1Container container = new V1Container().name("lobby-server-container").image(Configs.getConfig().getLobbyImage()).env(envVars).imagePullPolicy("Always").ports(Collections.singletonList(new V1ContainerPort().containerPort(port).protocol("TCP")));
    containers.add(container);

    V1Pod lobbyServerPod = new V1Pod().apiVersion("v1").kind("Pod").metadata(new V1ObjectMeta().name("lobby." + uuid).labels(labels)).spec(new V1PodSpec().containers(containers));
    api.createNamespacedPod("default", lobbyServerPod, null, null, null, null);

    V1Service lobbyServerService = new V1Service()
        .apiVersion("v1")
        .kind("Service")
        .metadata(new V1ObjectMeta().name("lobby-service-" + uuid))
        .spec(new V1ServiceSpec()
                .selector(labels)
                .ports(Collections.singletonList(new V1ServicePort().protocol("TCP").port(port).targetPort(new IntOrString(port))))
                .type("LoadBalancer"));

    api.createNamespacedService("default", lobbyServerService, null, null, null, null);

    DarkProxyManager.getLogger().info(LoggerColors.GREEN_BOLD + "Lobby server " + uuid + " created successfully!" + "\u001B[0m");

    PendingLobbyServers.add(uuid, DarkProxyManager);
  }

  public static void deleteLobbyServer(UUID uuid, DarkProxyManager DarkProxyManager) throws IOException, ApiException {
    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);
    CoreV1Api api = new CoreV1Api();

    api.deleteNamespacedPod("lobby." + uuid, "default", null, null, null, null, null, null);
    api.deleteNamespacedService("lobby-service-" + uuid, "default", null, null, null, null, null, null);

    DarkProxyManager.getLogger().info(LoggerColors.RED_BOLD + "Lobby server " + uuid + " deleted successfully!" + "\u001B[0m");

    PendingLobbyServers.getList().remove(uuid);
  }
}
