package vakiliner.chatmoderator.spigot;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.spigot.SpigotParser;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;

public class SpigotChatModerator extends BukkitChatModerator {
	public void broadcast(ChatComponent component, boolean admins) {
		Set<CommandSender> recipients = new HashSet<>();
		String permission = admins ? Server.BROADCAST_CHANNEL_ADMINISTRATIVE : Server.BROADCAST_CHANNEL_USERS;
		for (Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(permission)) {
			if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
				recipients.add((CommandSender) permissible);
			}
		}
		for (CommandSender recipient : recipients) {
			recipient.spigot().sendMessage(SpigotParser.spigot(component));
		}
	}
}