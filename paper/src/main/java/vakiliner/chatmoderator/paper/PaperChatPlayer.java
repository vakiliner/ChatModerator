package vakiliner.chatmoderator.paper;

import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.paper.PaperParser;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class PaperChatPlayer extends vakiliner.chatcomponentapi.paper.PaperChatPlayer implements ChatPlayer {
	private final PaperChatModerator manager;

	public PaperChatPlayer(PaperChatModerator manager, Player player) {
		super((PaperParser) PaperChatModerator.PARSER, player);
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