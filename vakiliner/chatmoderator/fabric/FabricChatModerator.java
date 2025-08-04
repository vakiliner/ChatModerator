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
import net.fabricmc.loader.api.ModContainer;
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
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.AutoModerationRule.Actions;
import vakiliner.chatmoderator.fabric.event.AutoModerationTriggerCallback;

public class FabricChatModerator extends ChatModerator {
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final FabricParser PARSER = ChatComponentAPIFabricLoader.load();
	public final ConfigImpl config = new ConfigImpl();
	private final ModContainer modContainer = FabricLoader.getInstance().getModContainer(ID).get();
	protected MinecraftServer server;
	private ChatModeratorModInitializer modInitializer;

	void init(ChatModeratorModInitializer modInitializer) {
		this.modInitializer = modInitializer;
		super.init(modInitializer);
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

	public String getVersion() {
		return this.modContainer.getMetadata().getVersion().toString();
	}

	public String getName() {
		String name = this.modContainer.getMetadata().getName();
		return name != null ? name : this.modContainer.getMetadata().getId();
	}

	protected boolean automodTrigger(ChatPlayer player, CheckResult checkResult, Actions actions) {
		return AutoModerationTriggerCallback.EVENT.invoker().trigger(((vakiliner.chatcomponentapi.fabric.FabricChatPlayer) player).getPlayer(), checkResult, actions, true);
	}

	public MinecraftServer getServer() {
		return this.server;
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