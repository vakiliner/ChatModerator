package vakiliner.chatmoderator.paper;

import org.bukkit.Bukkit;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.spigot.SpigotChatModerator;

public class PaperChatModerator extends SpigotChatModerator {
	@Override
	public ChatOfflinePlayer getOfflinePlayerIfCached(String name) {
		return this.toChatOfflinePlayer(Bukkit.getOfflinePlayerIfCached(name));
	}
}
