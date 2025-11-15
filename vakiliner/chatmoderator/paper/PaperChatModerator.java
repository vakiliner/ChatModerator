package vakiliner.chatmoderator.paper;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.spigot.SpigotChatModerator;

public class PaperChatModerator extends SpigotChatModerator {
	public ChatOfflinePlayer getOfflinePlayerIfCached(String name) {
		OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
		return player != null ? this.toChatOfflinePlayer(player) : null;
	}
}