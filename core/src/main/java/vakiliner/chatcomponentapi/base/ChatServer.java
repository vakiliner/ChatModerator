package vakiliner.chatcomponentapi.base;

public interface ChatServer extends ChatCommandSender {
	ChatPlayerList getPlayerList();

	@Override
	default boolean isConsole() {
		return true;
	}

	void execute(IChatPlugin plugin, Runnable runnable);
}
