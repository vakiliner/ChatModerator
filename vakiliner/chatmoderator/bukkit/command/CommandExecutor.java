package vakiliner.chatmoderator.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;
import vakiliner.chatmoderator.bukkit.exception.CommandException;

public abstract class CommandExecutor implements TabExecutor {
	protected final BukkitChatModerator manager;

	public CommandExecutor(BukkitChatModerator manager) {
		this.manager = manager;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		ChatCommandSender chatCommandSender = this.manager.toChatCommandSender(sender);
		try {
			this.execute(chatCommandSender, args);
		} catch (CommandException err) {
			this.handleException(err, chatCommandSender, label, args);
		}
		return true;
	}

	public abstract void execute(ChatCommandSender sender, String[] args) throws CommandException;

	private void handleException(CommandException exception, ChatCommandSender sender, String lalel, String[] args) {
		final String fullCommand;
		if (args.length == 0) {
			fullCommand = lalel;
		} else {
			fullCommand = lalel + ' ' + String.join(" ", args);
		}
		sender.sendMessage(exception.getErrorComponent());
		sender.sendMessage(exception.getCommandComponent(fullCommand));
	}
}