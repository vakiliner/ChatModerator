package vakiliner.chatmoderator.base;

import java.util.Date;
import vakiliner.chatmoderator.core.MutedPlayer;

public interface ChatOfflinePlayer extends vakiliner.chatcomponentapi.base.ChatOfflinePlayer {
	default MutedPlayer getMute() {
		return this.getMute(true);
	}

	MutedPlayer getMute(Date filterExpired);

	default MutedPlayer getMute(boolean ignoreExpired) {
		return this.getMute(ignoreExpired ? new Date() : null);
	}

	default boolean isMuted() {
		return this.getMute(true) != null;
	}

	boolean isBypassModeration();

	boolean isBypassMutes();
}