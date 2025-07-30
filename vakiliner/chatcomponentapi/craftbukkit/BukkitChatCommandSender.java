package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitChatCommandSender implements ChatCommandSender {
	protected final BukkitParser parser;
	protected final CommandSender sender;

	public BukkitChatCommandSender(BukkitParser parser, CommandSender sender) {
		this.parser = Objects.requireNonNull(parser);
		this.sender = Objects.requireNonNull(sender);
	}

	public String getName() {
		return this.sender.getName();
	}

	public boolean isConsole() {
		return this.sender instanceof ConsoleCommandSender;
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.sender, component, type, uuid);
	}
}