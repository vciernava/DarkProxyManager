package cz.darklabs.darkproxymanager.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import cz.darklabs.darkproxymanager.DarkProxyManager;
import cz.darklabs.darkproxymanager.ServerManager;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import net.kyori.adventure.text.Component;

import java.io.IOException;


public final class TPS implements SimpleCommand {

    public TPS(DarkProxyManager DarkProxyManager) {
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();

        try {
            V1PodList pods = ServerManager.getPods();

            for (V1Pod pod : pods.getItems()) {
                V1ResourceRequirements resources = pod.getSpec().getContainers().get(0).getResources();
                source.sendMessage(Component.text(resources.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
