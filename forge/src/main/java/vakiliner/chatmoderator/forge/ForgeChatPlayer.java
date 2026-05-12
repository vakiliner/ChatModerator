package vakiliner.chatmoderator.forge;

import java.util.Date;
import net.minecraft.entity.player.ServerPlayerEntity;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatServer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class ForgeChatPlayer extends vakiliner.chatcomponentapi.forge.ForgeChatPlayer implements ChatPlayer {
	private final ForgeChatModerator manager;

	public ForgeChatPlayer(ForgeChatModerator manager, ServerPlayerEntity player) {
		super(ForgeChatModerator.PARSER, player);
		this.manager = manager;
	}

	public ChatServer getServer() {
		return this.manager.toChatServer(this.player.server);
	}

	public MutedPlayer getMute(Date filterExpired) {
		return this.manager.mutes.get(this.getUniqueId(), filterExpired);
	}

	public boolean isBypassModeration() {
		return this.server.isSingleplayerOwner(this.player.getGameProfile());
	}

	public boolean isBypassMutes() {
		return this.isOp();
	}
}