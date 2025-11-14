package vakiliner.chatmoderator.bukkit;

import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	private FileConfiguration configuration;

	void reload(FileConfiguration configuration) {
		this.configuration = configuration;
	}

	public int version() {
		return this.configuration.getInt("version", Short.MIN_VALUE);
	}

	public void version(int version) {
		this.configuration.set("version", version);
	}

	public int maxMessageLength() {
		return this.configuration.getInt("max_message_length", 128);
	}

	public void maxMessageLength(int length) {
		this.configuration.set("max_message_length", length);
	}

	public int maxMuteReasonLength() {
		return this.configuration.getInt("max_mute_reason_length", 64);
	}

	public void maxMuteReasonLength(int length) {
		this.configuration.set("max_mute_reason_length", length);
	}

	public boolean autoModerationEnabled() {
		return this.configuration.getBoolean("auto_moderation_enabled", false);
	}

	public void autoModerationEnabled(boolean enabled) {
		this.configuration.set("auto_moderation_enabled", enabled);
	}

	public boolean autoModerationUseThreadPool() {
		return this.configuration.getBoolean("auto_moderation_use_thread_pool", false);
	}

	public void autoModerationUseThreadPool(boolean enabled) {
		this.configuration.set("auto_moderation_use_thread_pool", enabled);
	}

	public boolean spectatorsChat() {
		return this.configuration.getBoolean("spectators_chat", false);
	}

	public void spectatorsChat(boolean enabled) {
		this.configuration.set("spectators_chat", enabled);
	}

	public boolean fixChat() {
		return this.configuration.getBoolean("fix_chat", false);
	}

	public void fixChat(boolean fix) {
		this.configuration.set("fix_chat", fix);
	}

	public String dictionaryFile() {
		return this.configuration.getString("dictionary_file", null);
	}

	public void dictionaryFile(String name) {
		this.configuration.set("dictionary_file", name);
	}

	public boolean showFailMessage() {
		return this.configuration.getBoolean("show_fail_message", true);
	}

	public void showFailMessage(boolean show) {
		this.configuration.set("show_fail_message", show);
	}

	public boolean logBlockedMessages() {
		return this.configuration.getBoolean("log_blocked_messages", false);
	}

	public void logBlockedMessages(boolean log) {
		this.configuration.set("log_blocked_messages", log);
	}

	public boolean logBlockedCommands() {
		return this.configuration.getBoolean("log_blocked_commands", false);
	}

	public void logBlockedCommands(boolean log) {
		this.configuration.set("log_blocked_commands", log);
	}

	public String message(String key, boolean required) {
		String message = this.messages().getString(key);
		if (required) Objects.requireNonNull(message, "Message not found");
		return message;
	}

	private ConfigurationSection messages() {
		return Objects.requireNonNull(this.configuration.getConfigurationSection("messages"), "Config not have a messages property");
	}

	public void messages(Map<String, String> message) {
		this.configuration.createSection("messages", message);
	}
}