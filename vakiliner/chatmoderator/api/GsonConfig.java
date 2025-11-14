package vakiliner.chatmoderator.api;

import java.util.Map;

public class GsonConfig {
	public Integer version;
	public Integer max_message_length;
	public Integer max_mute_reason_length;
	public Boolean auto_moderation_enabled;
	public Boolean auto_moderation_use_thread_pool;
	public Boolean spectators_chat;
	public String dictionary_file;
	public Boolean show_fail_message;
	public Boolean log_blocked_messages;
	public Boolean log_blocked_commands;
	public Map<String, String> messages;
}