package vakiliner.chatmoderator.fabric;

import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class FabricChatPlayer extends vakiliner.chatcomponentapi.fabric.FabricChatPlayer implements ChatPlayer {
	private final FabricChatModerator manager;

	public FabricChatPlayer(FabricChatModerator manager, ServerPlayer player) {
		super(FabricChatModerator.PARSER, player);
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