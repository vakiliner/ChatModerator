package vakiliner.chatcomponentapi.craftbukkit;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Team;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitParser extends BaseParser {
	protected static final boolean sendMessageWithUUID;

	static {
		Method method;
		try {
			method = CommandSender.class.getMethod("sendMessage", UUID.class, String.class);
		} catch (NoSuchMethodException err) {
			method = null;
		}
		sendMessageWithUUID = method != null;
	}

	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public boolean supportsFontInStyle() {
		return false;
	}

	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		this.sendMessage(sender, component.toLegacyText(), type == ChatMessageType.SYSTEM, uuid);
	}

	private void sendMessage(CommandSender sender, String message, boolean system, UUID uuid) {
		if (!system && sendMessageWithUUID) {
			sender.sendMessage(uuid, message);
		} else {
			sender.sendMessage(message);
		}
	}

	public void broadcastMessage(Server server, ChatComponent component, ChatMessageType type, UUID uuid) {
		Set<CommandSender> recipients = new HashSet<>();
		Set<Permissible> permissibles = server.getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_USERS);
		for (Permissible permissible : permissibles) {
			if (permissible instanceof CommandSender && permissible.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
				recipients.add((CommandSender) permissible);
			}
		}
		this.broadcast(recipients, component, type, uuid);
	}

	public void broadcast(Iterable<CommandSender> recipients, ChatComponent chatComponent, ChatMessageType chatMessageType, UUID uuid) {
		String message = chatComponent.toLegacyText();
		boolean system = chatMessageType == ChatMessageType.SYSTEM;
		for (CommandSender recipient : recipients) {
			this.sendMessage(recipient, message, system, uuid);
		}
	}

	public void execute(BukkitScheduler scheduler, IChatPlugin plugin, Runnable runnable) {
		if (plugin instanceof IBukkitChatPlugin) {
			if (!Bukkit.isPrimaryThread()) {
				scheduler.runTask(((IBukkitChatPlugin) plugin).asPlugin(), runnable);
			} else {
				runnable.run();
			}
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public static ChatColor bukkit(ChatTextFormat color) {
		return color != null ? ChatColor.getByChar(color.getChar()) : null;
	}

	public static ChatTextFormat bukkit(ChatColor color) {
		return color != null ? ChatTextFormat.getByChar(color.getChar()) : null;
	}

	public static NamespacedKey bukkit(ChatId chatId) {
		return chatId != null ? NamespacedKey.fromString(chatId.toString()) : null;
	}

	public static ChatId bukkit(NamespacedKey namespacedKey) {
		return namespacedKey != null ? new ChatId(namespacedKey.getNamespace(), namespacedKey.getKey()) : null;
	}

	public ChatPlayer toChatPlayer(Player player) {
		return player != null ? new BukkitChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(OfflinePlayer player) {
		if (player instanceof Player) {
			return this.toChatPlayer((Player) player);
		}
		return player != null ? new BukkitChatOfflinePlayer(this, player) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSender sender) {
		if (sender instanceof Player) {
			return this.toChatPlayer((Player) sender);
		}
		return sender != null ? new BukkitChatCommandSender(this, sender) : null;
	}

	public ChatTeam toChatTeam(Team team) {
		return team != null ? new BukkitChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(Server server) {
		return server != null ? new BukkitChatServer(this, server) : null;
	}

	public ChatPlayerList toChatPlayerList(Server server) {
		return server != null ? new BukkitChatServer(this, server) : null;
	}
}