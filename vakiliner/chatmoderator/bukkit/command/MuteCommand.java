package vakiliner.chatmoderator.bukkit.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public class MuteCommand implements TabExecutor {
	private final BukkitChatModerator manager;

	public MuteCommand(BukkitChatModerator manager) {
		this.manager = manager;
	}

	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (args.length < 2) return false;
		ChatCommandSender sender = this.manager.toChatCommandSender(commandSender);
		String targetName = args[0];
		String durationString = args[1];
		final Integer duration;
		if (durationString.equals("infinite")) {
			duration = null;
		} else {
			double d = 0;
			try {
				d = Double.parseDouble(durationString);
			} catch (NumberFormatException err) {
				d = 0;
			}
			if (d <= 0 || d * 10 % 1 != 0) return false;
			duration = (int) (d * 60);
		}
		String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : null;
		int maxMuteReasonLength = this.manager.getConfig().maxMuteReasonLength();
		if (reason != null && reason.length() > maxMuteReasonLength) {
			sender.sendMessage(new ChatTextComponent("The maximum length of reason is " + maxMuteReasonLength + ". You have reached " + reason.length(), ChatNamedColor.RED));
			return true;
		}
		ChatOfflinePlayer target = this.manager.getOfflinePlayerIfCached(targetName);
		if (target == null) {
			sender.sendMessage(new ChatTranslateComponent("No player was found", "argument.entity.notfound.player", ChatNamedColor.RED));
			return true;
		}
		if (target.isBypassMutes()) {
			sender.sendMessage(new ChatTextComponent("Cannot mute a player who can bypass mutes", ChatNamedColor.RED));
			return true;
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
			sender.sendMessage(new ChatTextComponent(target.getName() + " больше не может общаться"));
		} else {
			sender.sendMessage(new ChatTextComponent("This player is already muted", ChatNamedColor.RED));
		}
		return true;
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