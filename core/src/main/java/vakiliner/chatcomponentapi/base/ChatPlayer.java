package vakiliner.chatcomponentapi.base;

import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayer extends ChatOfflinePlayer, ChatCommandSender {
	ChatServer getServer();

	@Override
	default String getName() {
		return ChatOfflinePlayer.super.getName();
	}

	ChatComponent getDisplayName();

	ChatGameMode getGameMode();

	@Override
	default boolean isConsole() {
		return false;
	}
}
