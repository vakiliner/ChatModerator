package vakiliner.chatcomponentapi.base;

import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayer extends ChatOfflinePlayer, ChatCommandSender {
	ChatComponent getDisplayName();

	ChatGameMode getGameMode();

	default boolean isConsole() {
		return false;
	}
}