package vakiliner.chatmoderator.base;

import vakiliner.chatmoderator.core.MutedPlayer;

public interface ChatOfflinePlayer extends vakiliner.chatcomponentapi.base.ChatOfflinePlayer {
	default MutedPlayer getMute() {
		return this.getMute(true);
	}

	MutedPlayer getMute(boolean ignoreExpired);

	default boolean isMuted() {
		return this.getMute(true) != null;
	}

	boolean isBypassModeration();

	boolean isBypassMutes();
}