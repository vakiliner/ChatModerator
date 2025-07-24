package vakiliner.chatmoderator.bukkit.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;
import vakiliner.chatmoderator.exception.CommandException;
import vakiliner.chatmoderator.exception.UnknownArgumentException;
import vakiliner.chatmoderator.exception.UnknownCommandException;

public class MuteCommand extends CommandExecutor {
	public MuteCommand(BukkitChatModerator manager) {
		super(manager);
	}

	public void execute(ChatCommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) throw new UnknownCommandException();
		String targetName = args[0];
		String durationString = args[1];
		Integer duration;
		if (durationString.equals("infinite")) {
			duration = null;
		} else {
			double d = 0;
			try {
				d = Double.parseDouble(durationString);
			} catch (NumberFormatException err) {
				d = 0;
			}
			if (d <= 0 || d * 10 % 1 != 0) {
				throw new UnknownArgumentException(2);
			}
			duration = (int) (d * 60);
		}
		String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : null;
		int maxMuteReasonLength = this.manager.getConfig().maxMuteReasonLength();
		if (reason != null && reason.length() > maxMuteReasonLength) {
			sender.sendMessage(new ChatTextComponent("The maximum length of reason is " + maxMuteReasonLength + ". You have reached " + reason.length(), ChatNamedColor.RED));
			return;
		}
		ChatOfflinePlayer target = this.manager.getOfflinePlayerIfCached(targetName);
		if (target == null) {
			sender.sendMessage(new ChatTranslateComponent("No player was found", "argument.entity.notfound.player", ChatNamedColor.RED));
			return;
		}
		if (target.isBypassMutes()) {
			sender.sendMessage(new ChatTextComponent("Cannot mute a player who can bypass mutes", ChatNamedColor.RED));
			return;
		}
		final ModeratorType moderatorType;
		if (sender instanceof ChatPlayer) {
			moderatorType = ModeratorType.PLAYER;
		} else if (sender.isConsole()) {
			moderatorType = ModeratorType.SERVER;
		} else {
			moderatorType = ModeratorType.UNKNOWN;
		}
		if (this.manager.mutes.mute(target, sender.getName(), moderatorType, duration, reason)) {
			sender.sendMessage(target.getName() + " больше не может общаться");
		} else {
			sender.sendMessage(new ChatTextComponent("This player is already muted", ChatNamedColor.RED));
		}
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
			case 1:
				List<String> list = this.manager.getOnlinePlayers().stream().filter((player) -> !player.isMuted()).map(ChatPlayer::getName).collect(Collectors.toList());
				Collections.sort(list);
				return list;
			case 2: return Collections.singletonList("infinite");
			default: return null;
		}
	}
}