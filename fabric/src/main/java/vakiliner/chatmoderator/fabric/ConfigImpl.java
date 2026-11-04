package vakiliner.chatmoderator.fabric;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import vakiliner.chatmoderator.api.GsonConfig;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	protected GsonConfig config;

	void reload(GsonConfig config) {
		this.config = config;
	}

	@Override
	public int version() {
		return get(this.config.version, (int) Short.MIN_VALUE);
	}

	@Override
	public void version(int version) {
		this.config.version = version;
	}

	@Override
	public int maxMessageLength() {
		return get(this.config.max_message_length, 128);
	}

	@Override
	public void maxMessageLength(int length) {
		this.config.max_message_length = length;
	}

	@Override
	public int maxMuteReasonLength() {
		return get(this.config.max_mute_reason_length, 64);
	}

	@Override
	public void maxMuteReasonLength(int length) {
		this.config.max_mute_reason_length = length;
	}

	@Override
	public boolean autoModerationEnabled() {
		return get(this.config.auto_moderation_enabled, false);
	}

	@Override
	public void autoModerationEnabled(boolean enabled) {
		this.config.auto_moderation_enabled = enabled;
	}

	@Override
	public boolean autoModerationUseThreadPool() {
		return get(this.config.auto_moderation_use_thread_pool, false);
	}

	@Override
	public void autoModerationUseThreadPool(boolean enabled) {
		this.config.auto_moderation_use_thread_pool = enabled;
	}

	@Override
	public boolean spectatorsChat() {
		return get(this.config.spectators_chat, false);
	}

	@Override
	public void spectatorsChat(boolean enabled) {
		this.config.spectators_chat = enabled;
	}

	@Override
	public String folderPath() {
		return get(this.config.folder_path, null);
	}

	@Override
	public void folderPath(String path) {
		this.config.folder_path = path;
	}

	@Override
	public String mutesPath() {
		return get(this.config.mutes_path, "mutes.json");
	}

	@Override
	public void mutesPath(String path) {
		this.config.mutes_path = path;
	}

	@Override
	public String autoModerationRulesPath() {
		return get(this.config.auto_moderation_rules_path, "auto_moderation_rules.json");
	}

	@Override
	public void autoModerationRulesPath(String path) {
		this.config.auto_moderation_rules_path = path;
	}

	@Override
	public String dictionaryPath() {
		return get(this.config.dictionary_file, null);
	}

	@Override
	public void dictionaryPath(String name) {
		this.config.dictionary_file = name;
	}

	@Override
	public boolean showFailMessage() {
		return get(this.config.show_fail_message, true);
	}

	@Override
	public void showFailMessage(boolean show) {
		this.config.show_fail_message = show;
	}

	@Override
	public boolean logBlockedMessages() {
		return get(this.config.log_blocked_messages, false);
	}

	@Override
	public void logBlockedMessages(boolean log) {
		this.config.log_blocked_messages = log;
	}

	@Override
	public boolean logBlockedCommands() {
		return get(this.config.log_blocked_commands, false);
	}

	@Override
	public void logBlockedCommands(boolean log) {
		this.config.log_blocked_commands = log;
	}

	@Override
	public String message(String key, boolean required) {
		String message = this.messages().get(key);
		if (required) Objects.requireNonNull(message, "Message not found");
		return message;
	}

	private Map<String, String> messages() {
		return Objects.requireNonNull(this.config.messages, "Config not have a messages property");
	}

	@Override
	public void messages(Map<String, String> message) {
		this.config.messages = new HashMap<>(message);
	}

	private static <V> V get(V nullable, V orDefault) {
		return nullable != null ? nullable : orDefault;
	}
}
