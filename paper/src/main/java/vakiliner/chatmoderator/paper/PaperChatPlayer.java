package vakiliner.chatmoderator.paper;

import java.util.Date;
import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.paper.PaperParser;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatServer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class PaperChatPlayer extends vakiliner.chatcomponentapi.paper.PaperChatPlayer implements ChatPlayer {
	private final PaperChatModerator manager;

	public PaperChatPlayer(PaperChatModerator manager, Player player) {
		super((PaperParser) PaperChatModerator.PARSER, player);
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