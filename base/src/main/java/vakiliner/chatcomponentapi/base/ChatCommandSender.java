package vakiliner.chatcomponentapi.base;

import java.util.UUID;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatCommandSender {
	String getName();

	boolean isConsole();

	default void sendMessage(ChatComponent component) {
		this.sendMessage(component, ChatMessageType.SYSTEM, null);
	}

	void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid);
}