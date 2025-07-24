package vakiliner.chatcomponentapi.base;

import java.util.UUID;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatCommandSender {
	String getName();

	boolean isConsole();

	void sendMessage(String message);

	void sendMessage(ChatComponent component);

	void sendMessage(UUID uuid, String message);

	void sendMessage(UUID uuid, ChatComponent component);
}