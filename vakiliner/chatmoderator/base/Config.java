package vakiliner.chatmoderator.base;

import java.util.Map;

public interface Config {
	int version();

	void version(int version);

	int maxMessageLength();

	void maxMessageLength(int length);

	int maxMuteReasonLength();

	void maxMuteReasonLength(int length);

	boolean autoModerationEnabled();

	void autoModerationEnabled(boolean enabled);

	boolean autoModerationUseThreadPool();

	void autoModerationUseThreadPool(boolean enabled);

	boolean spectatorsChat();

	void spectatorsChat(boolean enabled);

	boolean fixChat();

	void fixChat(boolean fix);

	String dictionaryFile();

	void dictionaryFile(String name);

	boolean showFailMessage();

	void showFailMessage(boolean show);
	
	default String message(String key) {
		return this.message(key, true);
	}

	boolean logBlockedMessages();
	
	void logBlockedMessages(boolean log);

	String message(String key, boolean required);

	void messages(Map<String, String> message);
}