package vakiliner.chatmoderator.fabric;

import vakiliner.chatmoderator.api.GsonConfig;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	private GsonConfig config;

	void reload(GsonConfig config) {
		this.config = config;
	}

	public int maxMessageLength() {
		return get(this.config.max_message_length, 128);
	}

	public int maxMuteReasonLength() {
		return get(this.config.max_mute_reason_length, 64);
	}

	public boolean autoModerationEnabled() {
		return get(this.config.auto_moderation_enabled, false);
	}

	public boolean autoModerationUseThreadPool() {
		return get(this.config.auto_moderation_use_thread_pool, false);
	}

	public boolean spectatorsChat() {
		return get(this.config.spectators_chat, false);
	}

	public boolean fixChat() {
		return false;
	}

	public String dictionaryFile() {
		return get(this.config.dictionary_file, null);
	}

	private static <V> V get(V nullable, V orDefault) {
		return nullable != null ? nullable : orDefault;
	}
}