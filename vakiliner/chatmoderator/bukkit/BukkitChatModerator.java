package vakiliner.chatmoderator.bukkit;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import vakiliner.chatcomponentapi.ChatComponentAPIBukkitLoader;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitChatCommandSender;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.Config;

public class BukkitChatModerator extends ChatModerator {
	public static final String SPECTATORS_CHAT = "chatmoderator.spectator_chat";
	public static final BukkitParser PARSER = ChatComponentAPIBukkitLoader.load();
	public final ConfigImpl config = new ConfigImpl();
	private ChatModeratorPlugin plugin;

	void init(ChatModeratorPlugin plugin) {
		this.plugin = plugin;
	}

	public ChatModeratorPlugin getPlugin() {
		return this.plugin;
	}

	protected BukkitListener createListener() {
		return new BukkitListener(this);
	}

	public Config getConfig() {
		return this.config;
	}

	protected File getDataFolder() {
		return this.plugin.getDataFolder();
	}

	public Path getConfigPath() {
		return this.plugin.getDataFolder().toPath().resolve("config.yml");
	}

	public ChatOfflinePlayer getOfflinePlayerIfCached(String name) {
		OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
		if (player == null) {
			return null;
		}
		return this.toChatOfflinePlayer(player);
	}

	@SuppressWarnings("deprecation")
	public void broadcast(ChatComponent component, boolean admins) {
		Bukkit.broadcast(component.toLegacyText(), admins ? Server.BROADCAST_CHANNEL_ADMINISTRATIVE : Server.BROADCAST_CHANNEL_USERS);
	}

	public Collection<ChatPlayer> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers().stream().map(this::toChatPlayer).collect(Collectors.toList());
	}

	protected void spectatorsChat(ChatTranslateComponent component) {
		Set<ChatCommandSender> recipients = new HashSet<>();
		for (Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(SPECTATORS_CHAT)) {
			if (permissible instanceof CommandSender && permissible.hasPermission(SPECTATORS_CHAT)) {
				recipients.add(this.toChatCommandSender((CommandSender) permissible));
			}
		}
		for (ChatCommandSender recipient : recipients) {
			recipient.sendMessage(component);
		}
	}

	public ChatPlayer toChatPlayer(Player player) {
		return player != null ? new BukkitChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(OfflinePlayer player) {
		if (player instanceof Player) {
			return this.toChatPlayer(((Player) player));
		}
		return player != null ? new BukkitChatOfflinePlayer(this, player) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSender sender) {
		if (sender instanceof OfflinePlayer) {
			return (ChatCommandSender) this.toChatOfflinePlayer((OfflinePlayer) sender);
		}
		return sender != null ? new BukkitChatCommandSender(PARSER, sender) : null;
	}
}