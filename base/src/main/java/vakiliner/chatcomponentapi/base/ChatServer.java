package vakiliner.chatcomponentapi.base;

public interface ChatServer extends ChatCommandSender {
	ChatPlayerList getPlayerList();

	default boolean isConsole() {
		return true;
	}

	void execute(IChatPlugin plugin, Runnable runnable);
}