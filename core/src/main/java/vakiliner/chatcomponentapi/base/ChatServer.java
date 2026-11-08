package vakiliner.chatcomponentapi.base;

import java.util.concurrent.Executor;

public interface ChatServer extends Executor, ChatCommandSender {
	ChatPlayerList getPlayerList();

	@Override
	default boolean isConsole() {
		return true;
	}

	@Override
	void execute(Runnable command);
}
