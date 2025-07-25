package vakiliner.chatmoderator.paper;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.paper.PaperParser;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.spigot.SpigotChatModerator;

public class PaperChatModerator extends SpigotChatModerator {
	public void broadcast(ChatComponent component, boolean admins) {
		Bukkit.broadcast(PaperParser.paper(component), admins ? Server.BROADCAST_CHANNEL_ADMINISTRATIVE : Server.BROADCAST_CHANNEL_USERS);
	}

	public ChatOfflinePlayer getOfflinePlayerIfCached(String name) {
		OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
		return player != null ? this.toChatOfflinePlayer(player) : null;
	}
}