package vakiliner.chatcomponentapi.base;

public interface ChatServer {
	ChatPlayerList getPlayerList();

	void execute(IChatPlugin plugin, Runnable runnable);
}