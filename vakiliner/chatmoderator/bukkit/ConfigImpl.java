package vakiliner.chatmoderator.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	private FileConfiguration configuration;

	void reload(FileConfiguration configuration) {
		this.configuration = configuration;
	}

	public int maxMessageLength() {
		return this.configuration.getInt("max_message_length", 128);
	}

	public int maxMuteReasonLength() {
		return this.configuration.getInt("max_mute_reason_length", 64);
	}

	public boolean autoModerationEnabled() {
		return this.configuration.getBoolean("auto_moderation_enabled", false);
	}

	public boolean autoModerationUseThreadPool() {
		return this.configuration.getBoolean("auto_moderation_use_thread_pool", false);
	}

	public boolean spectatorsChat() {
		return this.configuration.getBoolean("spectators_chat", false);
	}

	public boolean fixChat() {
		return this.configuration.getBoolean("fix_chat", false);
	}

	public String dictionaryFile() {
		return this.configuration.getString("dictionary_file", null);
	}
}