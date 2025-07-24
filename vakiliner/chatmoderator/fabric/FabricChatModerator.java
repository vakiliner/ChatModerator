package vakiliner.chatmoderator.fabric;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatcomponentapi.ChatComponentAPIFabricLoader;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.fabric.FabricChatCommandSender;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;

public class FabricChatModerator extends ChatModerator {
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final FabricParser PARSER = ChatComponentAPIFabricLoader.load();
	public final ConfigImpl config = new ConfigImpl();
	protected MinecraftServer server;
	private ChatModeratorModInitializer modInitializer;

	void init(ChatModeratorModInitializer modInitializer) {
		this.modInitializer = modInitializer;
	}

	public ChatModeratorModInitializer getModInitializer() {
		return this.modInitializer;
	}

	protected FabricListener createListener() {
		return new FabricListener(this);
	}

	public ConfigImpl getConfig() {
		return this.config;
	}

	protected File getDataFolder() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("ChatModerator");
		if (!path.toFile().exists()) {
			try {
				Files.createDirectories(path);
			} catch (IOException err) {
				throw new RuntimeException("Creating data folder", err);
			}
		}
		return path.toFile();
	}

	public Path getConfigPath() {
		return this.getDataFolder().toPath().resolve("config.json");
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public ChatOfflinePlayer getOfflinePlayerIfCached(String name) {
		ServerPlayer player = this.server.getPlayerList().getPlayerByName(name);
		if (player != null) {
			return this.toChatPlayer(player);
		}
		return this.toChatOfflinePlayer(this.server.getProfileCache().get(name));
	}

	public void broadcast(ChatComponent component, boolean admins) {
		Set<ChatCommandSender> recipients = new HashSet<>();
		recipients.add(PARSER.toChatCommandSender(this.server));
		for (ChatPlayer player : this.getOnlinePlayers()) {
			if (admins && !player.isOp()) continue;
			recipients.add(player);
		}
		for (ChatCommandSender recipient : recipients) {
			recipient.sendMessage(component);
		}
	}

	public Collection<ChatPlayer> getOnlinePlayers() {
		return this.server.getPlayerList().getPlayers().stream().map(this::toChatPlayer).collect(Collectors.toList());
	}

	protected void spectatorsChat(ChatTranslateComponent component) {
		Set<ChatCommandSender> recipients = new HashSet<>();
		recipients.add(PARSER.toChatCommandSender(this.server));
		for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
			if (player.isSpectator()) {
				recipients.add(this.toChatPlayer(player));
			}
		}
		for (ChatCommandSender recipient : recipients) {
			recipient.sendMessage(component);
		}
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new FabricChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(GameProfile gameProfile) {
		return gameProfile != null ? new FabricChatOfflinePlayer(this, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSource sender) {
		if (sender instanceof ServerPlayer) {
			return (ChatCommandSender) this.toChatPlayer((ServerPlayer) sender);
		}
		return sender != null ? new FabricChatCommandSender(PARSER, sender) : null;
	}
}