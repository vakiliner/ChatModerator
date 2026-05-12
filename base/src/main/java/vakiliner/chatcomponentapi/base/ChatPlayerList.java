package vakiliner.chatcomponentapi.base;

import java.util.Collection;
import java.util.UUID;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatPlayerList {
	ChatServer getServer();

	Collection<? extends ChatPlayer> getPlayers();

	ChatPlayer getPlayer(UUID uuid);

	ChatPlayer getPlayer(String name);

	default void broadcastMessage(ChatComponent component) {
		this.broadcastMessage(component, ChatMessageType.SYSTEM, null);
	}

	void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid);
}