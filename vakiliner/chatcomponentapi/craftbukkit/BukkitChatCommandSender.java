package vakiliner.chatcomponentapi.craftbukkit;

import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitChatCommandSender implements ChatCommandSender {
	protected final BukkitParser parser;
	protected final CommandSender sender;

	public BukkitChatCommandSender(BukkitParser parser, CommandSender sender) {
		this.parser = parser;
		this.sender = sender;
	}

	public String getName() {
		return this.sender.getName();
	}

	public boolean isConsole() {
		return this.sender instanceof ConsoleCommandSender;
	}

	public void sendMessage(String message) {
		this.sender.sendMessage(message);
	}

	public void sendMessage(UUID uuid, String message) {
		this.sender.sendMessage(uuid, message);
	}

	public void sendMessage(ChatComponent component) {
		this.parser.sendMessage(this.sender, component);
	}

	public void sendMessage(UUID uuid, ChatComponent component) {
		this.parser.sendMessage(this.sender, uuid, component);
	}
}