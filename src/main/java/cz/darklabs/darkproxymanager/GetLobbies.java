package cz.darklabs.darkproxymanager;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


public final class GetLobbies implements SimpleCommand {

  private DarkProxyManager DarkProxyManager;

  public GetLobbies(DarkProxyManager DarkProxyManager) {
    this.DarkProxyManager = DarkProxyManager;
  }

  @Override
  public void execute(final Invocation invocation) {
    CommandSource source = invocation.source();
    String[] args = invocation.arguments();

    source.sendMessage(Component.text("You ran the command with the following arguments: ", NamedTextColor.GREEN)
        .append(Component.text(String.join(" ", args), NamedTextColor.WHITE)));
  }
}
