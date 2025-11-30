package vakiliner.chatmoderator.fabric;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatcomponentapi.ChatComponentAPIFabricLoader;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
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
	private final ModContainer modContainer = FabricLoader.getInstance().getModContainer(ID).get();
	protected MinecraftServer server;
	private ChatModeratorModInitializer modInitializer;

	void init(ChatModeratorModInitializer modInitializer) {
		this.modInitializer = modInitializer;
		super.init(this.modInitializer);
		try {
			this.reloadConfig();
		} catch (IOException err) {
			throw new RuntimeException(err);
		}
		this.setup(this.modInitializer);
	}

	public void saveConfig() throws IOException {
		Files.write(MANAGER.getConfigPath(), new Gson().toJson(this.config.config).getBytes(StandardCharsets.UTF_8));
	}

	public void reloadConfig() throws IOException {
		Path path = MANAGER.getConfigPath();
		GsonConfig config;
		if (path.toFile().exists()) {
			try {
				config = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonConfig.class);
			} catch (IOException err) {
				throw err;
			}
		} else {
			config = null;
		}
		this.config.reload(config);
		if (this.checkConfigUpdates()) {
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
		String name = this.modContainer.getMetadata().getName();
		return name != null ? name : this.modContainer.getMetadata().getId();
	}

	protected boolean automodTrigger(ChatPlayer player, CheckResult checkResult, MessageActions actions) {
		return AutoModerationTriggerCallback.EVENT.invoker().trigger(((vakiliner.chatcomponentapi.fabric.FabricChatPlayer) player).getPlayer(), checkResult, actions, true);
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public void broadcast(ChatComponent component, boolean adminMessage) {
		Set<ChatPlayer> admins = new HashSet<>();
		for (ChatPlayer player : this.getOnlinePlayers()) {
			if (adminMessage && !player.isOp()) continue;
			admins.add(player);
		}
		this.toChatCommandSender(this.server).sendMessage(new ChatTextComponent(component.toLegacyText()));
		admins.forEach((p) -> p.sendMessage(component));
	}

	public Collection<ChatPlayer> getOnlinePlayers() {
		return this.server.getPlayerList().getPlayers().stream().map(this::toChatPlayer).collect(Collectors.toList());
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new FabricChatPlayer(this, player) : null;
	}

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