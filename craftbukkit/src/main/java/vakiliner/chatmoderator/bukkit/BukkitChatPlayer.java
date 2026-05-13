package vakiliner.chatmoderator.bukkit;

import java.util.Date;
import org.bukkit.entity.Player;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatServer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class BukkitChatPlayer extends vakiliner.chatcomponentapi.craftbukkit.BukkitChatPlayer implements ChatPlayer {
	private final BukkitChatModerator manager;

	public BukkitChatPlayer(BukkitChatModerator manager, Player player) {
		super(BukkitChatModerator.PARSER, player);
		this.manager = manager;
	}

	public ChatServer getServer() {
		return this.manager.toChatServer(this.getPlayer().getServer());
	}

	public MutedPlayer getMute(Date filterExpired) {
		return this.manager.mutes.get(this.getUniqueId(), filterExpired);
	}

	public boolean isBypassModeration() {
		return this.getPlayer().hasPermission("chatmoderator.bypass_moderation.*");
	}

	public boolean isBypassMutes() {
		return this.getPlayer().hasPermission("chatmoderator.bypass_moderation.mutes");
	}
}