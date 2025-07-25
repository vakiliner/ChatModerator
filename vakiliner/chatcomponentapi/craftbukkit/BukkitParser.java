package vakiliner.chatcomponentapi.craftbukkit;

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
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitParser extends BaseParser {
	public void sendMessage(CommandSender sender, ChatComponent component) {
		sender.sendMessage(component.toLegacyText());
	}

	public void sendMessage(CommandSender sender, UUID uuid, ChatComponent component) {
		sender.sendMessage(uuid, component.toLegacyText());
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