package vakiliner.chatmoderator.base;

public interface Config {
	int maxMessageLength();

	int maxMuteReasonLength();

	boolean autoModerationEnabled();

	boolean autoModerationUseThreadPool();

	boolean spectatorsChat();

	boolean fixChat();

	String dictionaryFile();
}