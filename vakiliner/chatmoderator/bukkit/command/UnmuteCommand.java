package vakiliner.chatmoderator.bukkit.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;
import vakiliner.chatmoderator.bukkit.exception.CommandException;
import vakiliner.chatmoderator.bukkit.exception.UnknownCommandException;
import vakiliner.chatmoderator.core.MutedPlayer;

public class UnmuteCommand extends CommandExecutor {
	public UnmuteCommand(BukkitChatModerator manager) {
		super(manager);
	}

	public void execute(ChatCommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) throw new UnknownCommandException();
		String targetName = args[0];
		MutedPlayer mute = this.manager.mutes.getMutedPlayer(targetName);
		if (mute != null && this.manager.mutes.unmute(mute.getUniqueId())) {
			sender.sendMessage(mute.getName() + " теперь снова может общаться");
		} else {
			sender.sendMessage(new ChatTextComponent("This player is not muted", ChatNamedColor.RED));
		}
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) return Collections.emptyList();
		List<String> list = this.manager.mutes.map().values().stream().map(MutedPlayer::getName).collect(Collectors.toList());
		Collections.sort(list);
		return list;
	}
}