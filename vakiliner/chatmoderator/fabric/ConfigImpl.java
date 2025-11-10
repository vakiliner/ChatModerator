package vakiliner.chatmoderator.fabric;

import java.util.HashMap;
import java.util.Map;
import vakiliner.chatmoderator.api.GsonConfig;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	protected GsonConfig config;

	void reload(GsonConfig config) {
		this.config = config;
	}

	public int version() {
		return get(this.config.version, (int) Short.MIN_VALUE);
	}

	public void version(int version) {
		this.config.version = version;
	}

	public int maxMessageLength() {
		return get(this.config.max_message_length, 128);
	}

	public void maxMessageLength(int length) {
		this.config.max_message_length = length;
	}

	public int maxMuteReasonLength() {
		return get(this.config.max_mute_reason_length, 64);
	}

	public void maxMuteReasonLength(int length) {
		this.config.max_mute_reason_length = length;
	}

	public boolean autoModerationEnabled() {
		return get(this.config.auto_moderation_enabled, false);
	}

	public void autoModerationEnabled(boolean enabled) {
		this.config.auto_moderation_enabled = enabled;
	}

	public boolean autoModerationUseThreadPool() {
		return get(this.config.auto_moderation_use_thread_pool, false);
	}

	public void autoModerationUseThreadPool(boolean enabled) {
		this.config.auto_moderation_use_thread_pool = enabled;
	}

	public boolean spectatorsChat() {
		return get(this.config.spectators_chat, false);
	}

	public void spectatorsChat(boolean enabled) {
		this.config.spectators_chat = enabled;
	}

	public boolean fixChat() {
		return false;
	}

	public void fixChat(boolean fix) {
		throw new UnsupportedOperationException("Unrelized");
	}

	public String dictionaryFile() {
		return get(this.config.dictionary_file, null);
	}

	public void dictionaryFile(String name) {
		this.config.dictionary_file = name;
	}

	public boolean showFailMessage() {
		return get(this.config.show_fail_message, true);
	}

	public void showFailMessage(boolean show) {
		this.config.show_fail_message = show;
	}

	public boolean logBlockedMessages() {
		return get(this.config.log_blocked_messages, false);
	}

	public void logBlockedMessages(boolean log) {
		this.config.log_blocked_messages = log;
	}

	public String message(String key) {
		return this.config.messages.get(key);
	}

	public void messages(Map<String, String> message) {
		this.config.messages = new HashMap<>(message);
	}

	private static <V> V get(V nullable, V orDefault) {
		return nullable != null ? nullable : orDefault;
	}
}