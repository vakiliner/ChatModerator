package vakiliner.chatmoderator.bukkit;

import org.bukkit.entity.Player;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class BukkitChatPlayer extends vakiliner.chatcomponentapi.craftbukkit.BukkitChatPlayer implements ChatPlayer {
	private final BukkitChatModerator manager;

	public BukkitChatPlayer(BukkitChatModerator manager, Player player) {
		super(BukkitChatModerator.PARSER, player);
		this.manager = manager;
	}

	public MutedPlayer getMute(boolean ignoreExpired) {
		MutedPlayer mute = this.manager.mutes.get(this.getUniqueId());
		if (mute != null && !(ignoreExpired && mute.isExpired())) {
			return mute;
		}
		return null;
	}

	public boolean isBypassModeration() {
		return this.getPlayer().hasPermission("chatmoderator.bypass_moderation.*");
	}

	public boolean isBypassMutes() {
		return this.getPlayer().hasPermission("chatmoderator.bypass_moderation.mutes");
	}
}