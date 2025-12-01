package vakiliner.chatcomponentapi.craftbukkit;

import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitParser extends BaseParser {
	protected static final boolean sendMessageWithUUID;

	static {
		Class<CommandSender> clazz = CommandSender.class;
		Method method;
		try {
			method = clazz.getMethod("sendMessage", UUID.class, String.class);
		} catch (NoSuchMethodException err) {
			method = null;
		}
		sendMessageWithUUID = method != null;
	}

	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (sendMessageWithUUID && type == ChatMessageType.CHAT) {
			sender.sendMessage(uuid, component.toLegacyText());
		} else {
			sender.sendMessage(component.toLegacyText());
		}
	}

	public static ChatColor bukkit(ChatTextFormat color) {
		return color != null ? ChatColor.getByChar(color.getChar()) : null;
	}

	public static ChatTextFormat bukkit(ChatColor color) {
		return color != null ? ChatTextFormat.getByChar(color.getChar()) : null;
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
		if (sender instanceof Player) {
			return this.toChatPlayer((Player) sender);
		}
		return sender != null ? new BukkitChatCommandSender(this, sender) : null;
	}

	public ChatTeam toChatTeam(Team team) {
		return team != null ? new BukkitChatTeam(this, team) : null;
	}
}