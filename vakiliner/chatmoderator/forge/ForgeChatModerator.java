package vakiliner.chatmoderator.forge;

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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import vakiliner.chatcomponentapi.ChatComponentAPIForgeLoader;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.automod.MessageActions;
import vakiliner.chatmoderator.forge.event.AutoModerationTriggerEvent;

public class ForgeChatModerator extends ChatModerator {
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final ForgeParser PARSER = ChatComponentAPIForgeLoader.load();
	public final ConfigImpl config = ConfigImpl.config;
	protected MinecraftServer server;
	private ChatModeratorModInitializer modInitializer;

	void init(ChatModeratorModInitializer modInitializer) {
		this.modInitializer = modInitializer;
		super.init(modInitializer);
	}

	public ChatModeratorModInitializer getModInitializer() {
		return this.modInitializer;
	}

	protected ForgeListener createListener() {
		return new ForgeListener(this);
	}

	protected void log(String message) {
		LOGGER.info(message);
	}

	public ConfigImpl getConfig() {
		return this.config;
	}

	protected File getDataFolder() {
		Path path = new File(".").toPath().resolve("config").resolve("ChatModerator");
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
		return this.getDataFolder().toPath().resolve("config.toml");
	}

	public String getName() {
		String displayName = this.modInitializer.modContainer.getModInfo().getDisplayName();
		return displayName != null ? displayName : this.modInitializer.modContainer.getModId();
	}

	protected boolean automodTrigger(ChatPlayer player, CheckResult checkResult, MessageActions actions) {
		AutoModerationTriggerEvent event = new AutoModerationTriggerEvent(((vakiliner.chatcomponentapi.forge.ForgeChatPlayer) player).getPlayer(), checkResult, actions);
		return !MinecraftForge.EVENT_BUS.post(event);
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
		for (ServerPlayerEntity player : this.server.getPlayerList().getPlayers()) {
			if (player.isSpectator()) {
				recipients.add(this.toChatPlayer(player));
			}
		}
		for (ChatCommandSender recipient : recipients) {
			recipient.sendMessage(component);
		}
	}

	public ChatPlayer toChatPlayer(ServerPlayerEntity player) {
		return player != null ? new ForgeChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(GameProfile gameProfile) {
		return gameProfile != null ? new ForgeChatOfflinePlayer(this, gameProfile) : null;
	}
}