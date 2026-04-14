package vakiliner.chatmoderator.fabric;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatcomponentapi.ChatComponentAPIFabricLoader;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.fabric.FabricChatCommandSender;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatmoderator.api.GsonConfig;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.Config;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.automod.MessageActions;
import vakiliner.chatmoderator.fabric.event.AutoModerationTriggerCallback;

public class FabricChatModerator extends ChatModerator {
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final FabricParser PARSER = ChatComponentAPIFabricLoader.PARSER;
	public final ConfigImpl config = new ConfigImpl();
	protected MinecraftServer server;
	private ChatModeratorModInitializer modInitializer;

	void init(ChatModeratorModInitializer modInitializer) {
		this.modInitializer = modInitializer;
		try {
			this.reloadConfig();
		} catch (IOException err) {
			throw new RuntimeException(err);
		}
	}

	void startServer() {
		this.setup();
	}

	void stopServer() {
		this.stop();
	}

	public void saveConfig() throws IOException {
		Files.write(this.getConfigPath(), new GsonBuilder().setPrettyPrinting().create().toJson(this.config.config).getBytes(StandardCharsets.UTF_8));
	}

	public void reloadConfig() throws IOException {
		Path path = this.getConfigPath();
		if (path.toFile().exists()) {
			final GsonConfig config;
			try {
				config = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonConfig.class);
			} catch (IOException err) {
				throw err;
			}
			this.config.reload(config);
			if (this.checkConfigUpdates()) {
				this.saveConfig();
			}
		} else {
			this.config.reload(new GsonConfig());
			this.config.version(CONFIG_VERSION);
			this.config.maxMessageLength(80);
			this.config.maxMuteReasonLength(50);
			this.config.autoModerationEnabled(true);
			this.config.autoModerationUseThreadPool(false);
			this.config.spectatorsChat(false);
			this.config.dictionaryFile("dictionary_ru.json");
			this.config.showFailMessage(true);
			this.config.logBlockedMessages(false);
			this.config.logBlockedCommands(false);
			Map<String, String> messages = new HashMap<>();
			messages.put("fail_send_message", "Ваше сообщение не было отправлено");
			messages.put("fail_send_command", "Не удалось использовать команду");
			messages.put("fail_reasons.muted", "Вы были ограничены");
			messages.put("fail_reasons.muted_with_reason", "Вы были ограничены\nПричина: %s");
			messages.put("fail_reasons.cannot_use_msg_command_in_spectator", "Наблюдатели не могут использовать команды отправки сообщений");
			messages.put("fail_reasons.long_message", "Слишком длинное сообщение");
			messages.put("fail_reasons.automod_blocked_without_custom_message", "Публикация невозможна, поскольку сообщение содержит материалы, заблокированные этим сервером. Владельцы сервера также могут просматривать содержимое сообщений.");
			messages.put("fail_reasons.automod_blocked_with_custom_message", "Содержимое сообщения заблокировано сервером. Сообщение от модераторов: «%s»");
			this.config.messages(messages);
			this.saveConfig();
		}
	}

	public ChatModeratorModInitializer getModInitializer() {
		return this.modInitializer;
	}

	protected FabricListener createListener() {
		return new FabricListener(this);
	}

	protected void log(String message) {
		LOGGER.info(message);
	}

	public Config getConfig() {
		return this.config;
	}

	protected Path getFolderPath() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("ChatModerator");
		if (!path.toFile().exists()) {
			try {
				Files.createDirectories(path);
			} catch (IOException err) {
				throw new RuntimeException("Creating data folder", err);
			}
		}
		return path;
	}

	protected File getDataFolder() {
		return this.getFolderPath().toFile();
	}

	public Path getConfigPath() {
		return this.getFolderPath().resolve("config.json");
	}

	public String getName() {
		ModMetadata metadata = this.modInitializer.modContainer.getMetadata();
		String name = metadata.getName();
		return name != null ? name : metadata.getId();
	}

	protected boolean automodTrigger(ChatPlayer player, CheckResult checkResult, MessageActions actions) {
		return AutoModerationTriggerCallback.EVENT.invoker().trigger(((vakiliner.chatcomponentapi.fabric.FabricChatPlayer) player).getPlayer(), checkResult, actions, true);
	}

	@Deprecated
	public MinecraftServer getServer() {
		return this.server;
	}

	public void broadcast(ChatComponent component, boolean adminMessage) {
		Set<ChatCommandSender> admins = new HashSet<>();
		admins.add(this.toChatCommandSender(this.server));
		for (ChatPlayer player : this.getOnlinePlayers()) {
			if (adminMessage && !player.isOp()) continue;
			admins.add(player);
		}
		admins.forEach((p) -> p.sendMessage(component));
	}

	public Collection<ChatPlayer> getOnlinePlayers() {
		return this.server.getPlayerList().getPlayers().stream().map(this::toChatPlayer).collect(Collectors.toList());
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new FabricChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new FabricChatOfflinePlayer(this, server, gameProfile) : null;
	}

	@Deprecated
	public ChatOfflinePlayer toChatOfflinePlayer(GameProfile gameProfile) {
		return gameProfile != null ? new FabricChatOfflinePlayer(this, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSource sender) {
		if (sender instanceof ServerPlayer) {
			return this.toChatPlayer((ServerPlayer) sender);
		}
		return sender != null ? new FabricChatCommandSender(PARSER, sender) : null;
	}
}