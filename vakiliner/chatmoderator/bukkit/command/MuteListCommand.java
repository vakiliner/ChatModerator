package vakiliner.chatmoderator.bukkit.command;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.bukkit.BukkitChatModerator;
import vakiliner.chatmoderator.bukkit.exception.CommandException;
import vakiliner.chatmoderator.bukkit.exception.UnknownArgumentException;
import vakiliner.chatmoderator.bukkit.exception.UnknownCommandException;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public class MuteListCommand extends CommandExecutor {
	public MuteListCommand(BukkitChatModerator manager) {
		super(manager);
	}

	public void execute(ChatCommandSender sender, String[] args) throws CommandException {
		String pageString;
		if (args.length > 0) {
			pageString = args[0];
		} else {
			pageString = null;
		}
		Date now = new Date();
		if (pageString != null && pageString.equals("get")) {
			if (args.length < 2) throw new UnknownCommandException();
			String targetName = args[1];
			ChatOfflinePlayer player = this.manager.getOfflinePlayerIfCached(targetName);
			if (player == null) {
				sender.sendMessage(new ChatTranslateComponent("No player was found", "argument.entity.notfound.player", ChatNamedColor.RED));
				return;
			}
			MutedPlayer mute = this.manager.mutes.get(player.getUniqueId());
			ChatTextComponent result = new ChatTextComponent();
			result.append(ChatTextComponent.selector(player));
			if (mute == null || mute.isExpired(now)) {
				result.append(new ChatTextComponent(" не заглушён"));
			} else {
				ModeratorType moderatorType = mute.getModeratorType();
				switch (moderatorType) {
					case PLAYER:
					case SERVER:
					case PLUGIN:
						switch (moderatorType) {
							case PLAYER:
								result.append(new ChatTextComponent(" заглушён модератором " + mute.getModeratorName()));
								break;
							case SERVER:
								result.append(new ChatTextComponent(" заглушён сервером"));
								break;
							case PLUGIN:
								result.append(new ChatTextComponent(" заглушён плагином " + mute.getModeratorName()));
								break;
							default: throw new RuntimeException();
						}
						String reason = mute.getReason();
						if (reason != null) {
							result.append(new ChatTextComponent("\nПричина: "));
							result.append(new ChatTextComponent(reason));
						}
						break;
					case AUTOMOD:
						result.append(new ChatTextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
						break;
					default:
						result.append(new ChatTextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
						break;
				}
				Date expiration = mute.getExpirationAt();
				if (expiration != null) {
					result.append(new ChatTextComponent("\nЗаглушён временно, до: " + expiration.toString()));
				} else {
					result.append(new ChatTextComponent("\nЗаглушён навсегда"));
				}
			}
			sender.sendMessage(result);
		} else {
			int page = 1;
			if (pageString != null) {
				try {
					page = Integer.parseInt(pageString);
				} catch (NumberFormatException err) {
					throw new UnknownArgumentException(1);
				}
			}
			List<MutedPlayer> mutes = this.manager.mutes.map().values().stream().filter((mute) -> !mute.isExpired(now)).collect(Collectors.toList());
			int size = mutes.size();
			int pages = (size - 1) / 10 + 1;
			if (page < 1) {
				page = pages;
			} else if (page > pages) {
				page = 1;
			}
			ChatTextComponent border = new ChatTextComponent();
			border.append(button("[⏮]", 1, "First page"));
			border.append(new ChatTextComponent(" "));
			border.append(button("[⏪]", page - 1, "Previous page"));
			border.append(new ChatTextComponent(" "));
			border.append(new ChatTranslateComponent("Page %1$s of %2$s", "book.pageIndicator", new ChatTextComponent(Integer.toString(page)), new ChatTextComponent(Integer.toString(pages))));
			border.append(new ChatTextComponent(" "));
			border.append(button("[⏩]", page + 1, "Next page"));
			border.append(new ChatTextComponent(" "));
			border.append(button("[⏭]", pages, "Last page"));
			sender.sendMessage(border);
			if (!mutes.isEmpty()) {
				int a = page * 10;
				for (int i = (page - 1) * 10; a > i && size > i; i++) {
					MutedPlayer mute = mutes.get(i);
					ChatTextComponent component = new ChatTextComponent();
					ChatTextComponent unmute = new ChatTextComponent("[❌]");
					unmute.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, "/unmute " + mute.getName()));
					unmute.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("Unmute player")));
					component.append(unmute);
					component.append(new ChatTextComponent(" " + mute.getName()));
					ModeratorType moderatorType = mute.getModeratorType();
					switch (moderatorType) {
						case PLAYER:
							component.append(new ChatTextComponent(" заглушён модератором " + mute.getModeratorName()));
							break;
						case SERVER:
							component.append(new ChatTextComponent(" заглушён сервером"));
							break;
						case PLUGIN:
							component.append(new ChatTextComponent(" заглушён плагином " + mute.getModeratorName()));
							break;
						case AUTOMOD:
							component.append(new ChatTextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
							break;
						default:
							component.append(new ChatTextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
							break;
					}
					sender.sendMessage(component);
				}
			} else {
				sender.sendMessage(new ChatTextComponent("No mutes"));
			}
			sender.sendMessage(border);
		}
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
			case 1: return Collections.singletonList("get");
			case 2:
				if (args[0].equals("get")) {
					List<String> list = this.manager.mutes.map().values().stream().map(MutedPlayer::getName).collect(Collectors.toList());
					Collections.sort(list);
					return list;
				}
			default: return Collections.emptyList();
		}
	}

	private static ChatTextComponent button(String button, int page, String text) {
		ChatTextComponent component = new ChatTextComponent(button);
		component.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, "/mutes " + page));
		component.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent(text)));
		return component;
	}
}