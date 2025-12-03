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
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import vakiliner.chatcomponentapi.ChatComponentAPIForgeLoader;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.forge.ForgeChatCommandSender;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.Config;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.automod.MessageActions;
import vakiliner.chatmoderator.forge.event.AutoModerationTriggerEvent;

@EventBusSubscriber(modid = ForgeChatModerator.ID, bus = EventBusSubscriber.Bus.MOD)
public class ForgeChatModerator extends ChatModerator {
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final ForgeParser PARSER = ChatComponentAPIForgeLoader.PARSER;
	public final ConfigImpl config = ConfigImpl.config;
	protected MinecraftServer server;
	private ChatModeratorModInitializer modInitializer;

	void init(ChatModeratorModInitializer modInitializer) {
		this.modInitializer = modInitializer;
		super.init(this.modInitializer);
		ModContainer modContainer = this.modInitializer.modContainer;
		modContainer.addConfig(new ModConfig(ModConfig.Type.COMMON, ConfigImpl.configSpec, modContainer, "ChatModerator/config.toml"));
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

	public Config getConfig() {
		return this.config;
	}

	@SubscribeEvent
	public void onModConfigLoading(ModConfig.Loading reloading) {
		if (reloading.getConfig().getSpec() == ConfigImpl.configSpec) {
			this.checkConfigUpdates();
		}
	}

	@SubscribeEvent
	public void onFMLCommonSetup(FMLCommonSetupEvent event) {
		this.setup(this.modInitializer);
	}

	protected Path getFolderPath() {
		Path path = new File(".").toPath().resolve("config").resolve("ChatModerator");
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
		return this.getFolderPath().resolve("config.toml");
	}

	public String getName() {
		IModInfo modInfo = this.modInitializer.modContainer.getModInfo();
		String displayName = modInfo.getDisplayName();
		return displayName != null ? displayName : modInfo.getModId();
	}

	protected boolean automodTrigger(ChatPlayer player, CheckResult checkResult, MessageActions actions) {
		AutoModerationTriggerEvent event = new AutoModerationTriggerEvent(((vakiliner.chatcomponentapi.forge.ForgeChatPlayer) player).getPlayer(), checkResult, actions);
		return !MinecraftForge.EVENT_BUS.post(event);
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

	public ChatPlayer toChatPlayer(ServerPlayerEntity player) {
		return player != null ? new ForgeChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(GameProfile gameProfile) {
		return gameProfile != null ? new ForgeChatOfflinePlayer(this, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(ICommandSource sender) {
		if (sender instanceof ServerPlayerEntity) {
			return this.toChatPlayer((ServerPlayerEntity) sender);
		}
		return sender != null ? new ForgeChatCommandSender(PARSER, sender) : null;
	}
}