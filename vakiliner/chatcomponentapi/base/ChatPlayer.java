package vakiliner.chatcomponentapi.base;

import vakiliner.chatcomponentapi.common.ChatGameMode;

public interface ChatPlayer extends ChatOfflinePlayer, ChatCommandSender {
	ChatGameMode getGameMode();

	default boolean isConsole() {
		return false;
	}
}