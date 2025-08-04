package vakiliner.chatmoderator.forge;

import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import vakiliner.chatmoderator.base.Config;

class ConfigImpl implements Config {
	static final ForgeConfigSpec configSpec;
	static final ConfigImpl config;
	public final ConfigValue<String> version;
	public final IntValue maxMessageLength;
	public final IntValue maxMuteReasonLength;
	public final BooleanValue autoModerationEnabled;
	public final BooleanValue autoModerationUseThreadPool;
	public final BooleanValue spectatorsChat;
	public final BooleanValue fixChat;
	public final ConfigValue<String> dictionaryFile;
	public final BooleanValue showFailMessage;
	public final ConfigValue<String> rawMessages;

	public ConfigImpl(ForgeConfigSpec.Builder builder) {
		this.version = builder.translation("null").worldRestart().define("version", "0.1");
		this.maxMessageLength = builder.translation("null").defineInRange("max_message_length", 128, 0, 128);
		this.maxMuteReasonLength = builder.translation("null").defineInRange("max_mute_reason_length", 64, 0, 64);
		this.autoModerationEnabled = builder.translation("null").define("auto_moderation_enabled", false);
		this.autoModerationUseThreadPool = builder.translation("null").define("fix_chat", false);
		this.spectatorsChat = builder.translation("null").define("auto_moderation_use_thread_pool", false);
		this.fixChat = builder.translation("null").define("spectators_chat", false);
		this.dictionaryFile = builder.translation("null").define("dictionary_file", "");
		this.showFailMessage = builder.translation("null").define("show_fail_message", true);
		this.rawMessages = builder.translation("null").define("messages", "{}");
	}

	public String version() {
		return this.version.get();
	}

	public void version(String version) {
		this.version.set(version);
	}

	public int maxMessageLength() {
		return this.maxMessageLength.get();
	}

	public void maxMessageLength(int length) {
		this.maxMessageLength.set(length);
	}

	public int maxMuteReasonLength() {
		return this.maxMuteReasonLength.get();
	}

	public void maxMuteReasonLength(int length) {
		this.maxMuteReasonLength.set(length);
	}

	public boolean autoModerationEnabled() {
		return this.autoModerationEnabled.get();
	}

	public void autoModerationEnabled(boolean enabled) {
		this.autoModerationEnabled.set(enabled);
	}

	public boolean autoModerationUseThreadPool() {
		return this.autoModerationUseThreadPool.get();
	}

	public void autoModerationUseThreadPool(boolean enabled) {
		this.autoModerationUseThreadPool.set(enabled);
	}

	public boolean spectatorsChat() {
		return this.spectatorsChat.get();
	}

	public void spectatorsChat(boolean enabled) {
		this.spectatorsChat.set(enabled);
	}

	public boolean fixChat() {
		return false;
	}

	public void fixChat(boolean fix) {
		throw new UnsupportedOperationException("Unrelized");
	}

	public String dictionaryFile() {
		String file = this.dictionaryFile.get();
		return file.isEmpty() ? null : file;
	}

	public void dictionaryFile(String name) {
		this.dictionaryFile.set(name);
	}

	public boolean showFailMessage() {
		return this.showFailMessage.get();
	}

	public void showFailMessage(boolean show) {
		this.showFailMessage.set(show);
	}

	public String message(String key) {
		Map<String, String> map = new Gson().fromJson(this.rawMessages.get(), TypeToken.getParameterized(Map.class, String.class, String.class).getType());
		return map.get(key);
	}

	public void messages(Map<String, String> message) {
		this.rawMessages.set(new Gson().toJson(message));
	}

	static {
		Pair<ConfigImpl, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ConfigImpl::new);
		configSpec = pair.getRight();
		config = pair.getLeft();
	}
}