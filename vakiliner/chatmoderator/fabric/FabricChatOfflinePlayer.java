package vakiliner.chatmoderator.fabric;

import com.mojang.authlib.GameProfile;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class FabricChatOfflinePlayer extends vakiliner.chatcomponentapi.fabric.FabricChatOfflinePlayer implements ChatOfflinePlayer {
	private final FabricChatModerator manager;

	public FabricChatOfflinePlayer(FabricChatModerator manager, GameProfile gameProfile) {
		super(FabricChatModerator.PARSER, manager.getServer(), gameProfile);
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
		return false;
	}

	public boolean isBypassMutes() {
		return this.isOp();
	}
}