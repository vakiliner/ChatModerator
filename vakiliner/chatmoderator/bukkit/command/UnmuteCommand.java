package vakiliner.chatmoderator.bukkit.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;
import vakiliner.chatmoderator.core.MutedPlayer;

public class UnmuteCommand implements TabExecutor {
	private final BukkitChatModerator manager;

	public UnmuteCommand(BukkitChatModerator manager) {
		this.manager = manager;
	}

	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (args.length < 1) return false;
		ChatCommandSender sender = this.manager.toChatCommandSender(commandSender);
		String targetName = args[0];
		MutedPlayer mute = this.manager.mutes.getMutedPlayer(targetName);
		if (mute != null && this.manager.mutes.unmute(mute.getUniqueId())) {
			ChatTextComponent component = new ChatTextComponent();
			component.append(new ChatTextComponent(mute.getName()));
			component.append(new ChatTextComponent(" теперь снова может общаться"));
			sender.sendMessage(component);
		} else {
			sender.sendMessage(new ChatTextComponent("This player is not muted", ChatNamedColor.RED));
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) return Collections.emptyList();
		List<String> list = this.manager.mutes.map().values().stream().map(MutedPlayer::getName).collect(Collectors.toList());
		Collections.sort(list);
		return list;
	}
}