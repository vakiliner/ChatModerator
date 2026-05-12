package vakiliner.chatmoderator.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.spigot.SpigotChatModerator;

public class PaperChatModerator extends SpigotChatModerator {
	public ChatOfflinePlayer getOfflinePlayerIfCached(String name) {
		return this.toChatOfflinePlayer(Bukkit.getOfflinePlayerIfCached(name));
	}

	public ChatPlayer toChatPlayer(Player player) {
		return player != null ? new PaperChatPlayer(this, player) : null;
	}
}