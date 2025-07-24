package vakiliner.chatmoderator.forge;

import net.minecraft.entity.player.ServerPlayerEntity;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class ForgeChatPlayer extends vakiliner.chatcomponentapi.forge.ForgeChatPlayer implements ChatPlayer {
	private final ForgeChatModerator manager;

	public ForgeChatPlayer(ForgeChatModerator manager, ServerPlayerEntity player) {
		super(ForgeChatModerator.PARSER, player);
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
		return this.manager.server.isSingleplayerOwner(this.player.getGameProfile());
	}

	public boolean isBypassMutes() {
		return this.isOp();
	}
}