package vakiliner.chatmoderator.base;

public interface Config {
	int maxMessageLength();

	int maxMuteReasonLength();

	boolean autoModerationEnabled();

	boolean spectatorsChat();

	boolean fixChat();

	boolean autoModerationUseThreadPool();

	String dictionaryFile();
}