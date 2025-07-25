package vakiliner.chatmoderator.forge;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	static final ForgeConfigSpec configSpec;
	static final ConfigImpl config;
	public final IntValue maxMessageLength;
	public final IntValue maxMuteReasonLength;
	public final BooleanValue autoModerationEnabled;
	public final BooleanValue autoModerationUseThreadPool;
	public final BooleanValue spectatorsChat;
	public final BooleanValue fixChat;
	public final ConfigValue<String> dictionaryFile;

	public ConfigImpl(ForgeConfigSpec.Builder builder) {
		this.maxMessageLength = builder.translation("null").defineInRange("max_message_length", 128, 0, 128);
		this.maxMuteReasonLength = builder.translation("null").defineInRange("max_mute_reason_length", 64, 0, 64);
		this.autoModerationEnabled = builder.translation("null").define("auto_moderation_enabled", false);
		this.autoModerationUseThreadPool = builder.translation("null").define("fix_chat", false);
		this.spectatorsChat = builder.translation("null").define("auto_moderation_use_thread_pool", false);
		this.fixChat = builder.translation("null").define("spectators_chat", false);
		this.dictionaryFile = builder.translation("null").define("dictionary_file", "");
	}

	public int maxMessageLength() {
		return this.maxMessageLength.get();
	}

	public int maxMuteReasonLength() {
		return this.maxMuteReasonLength.get();
	}

	public boolean autoModerationEnabled() {
		return this.autoModerationEnabled.get();
	}

	public boolean autoModerationUseThreadPool() {
		return this.autoModerationUseThreadPool.get();
	}

	public boolean spectatorsChat() {
		return this.spectatorsChat.get();
	}

	public boolean fixChat() {
		return false;
	}

	public String dictionaryFile() {
		String file = this.dictionaryFile.get();
		return file.isEmpty() ? null : file;
	}

	static {
		Pair<ConfigImpl, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ConfigImpl::new);
		configSpec = pair.getRight();
		config = pair.getLeft();
	}
}