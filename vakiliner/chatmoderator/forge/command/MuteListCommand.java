package vakiliner.chatmoderator.forge.command;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;
import vakiliner.chatmoderator.forge.ChatModeratorModInitializer;
import vakiliner.chatmoderator.forge.ForgeChatModerator;

public class MuteListCommand {
	public static LiteralArgumentBuilder<CommandSource> register(ForgeChatModerator manager, CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> mutes = LiteralArgumentBuilder.literal("mutes");
		LiteralArgumentBuilder<CommandSource> get = LiteralArgumentBuilder.literal("get");
		return mutes.requires((stack) -> {
			return stack.hasPermission(3);
		}).executes((context) -> {
			return listMutes(context.getSource(), 1);
		}).then(get.then(Commands.argument("target", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
			Date now = new Date();
			return ISuggestionProvider.suggest(manager.mutes.map().values().stream().filter((mute) -> !mute.isExpired(now)).map(MutedPlayer::getName).collect(Collectors.toList()), builder);
		}).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			return getMute(context.getSource(), collection);
		}))).then(Commands.argument("page", IntegerArgumentType.integer(0)).executes((context) -> {
			int page = IntegerArgumentType.getInteger(context, "page");
			return listMutes(context.getSource(), page);
		}));
	}

	private static int listMutes(CommandSource stack, int page) throws CommandSyntaxException {
		ForgeChatModerator manager = ChatModeratorModInitializer.MANAGER;
		Date now = new Date();
		List<MutedPlayer> mutes = manager.mutes.map().values().stream().filter((mute) -> !mute.isExpired(now)).collect(Collectors.toList());
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
		stack.sendSuccess(ForgeParser.forge(border), false);
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
					case UNKNOWN:
					default:
						component.append(new ChatTextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
						break;
				}
				stack.sendSuccess(ForgeParser.forge(component), false);
			}
		} else {
			stack.sendSuccess(ForgeParser.forge(new ChatTextComponent("No mutes")), false);
		}
		stack.sendSuccess(ForgeParser.forge(border), false);
		return size;
	}

	private static ChatTextComponent button(String button, int page, String text) {
		ChatTextComponent component = new ChatTextComponent(button);
		component.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, "/mutes " + page));
		component.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent(text)));
		return component;
	}

	private static int getMute(CommandSource stack, Collection<GameProfile> collection) throws CommandSyntaxException {
		ForgeChatModerator manager = ChatModeratorModInitializer.MANAGER;
		int i = 0;
		Date now = new Date();
		for (GameProfile profile : collection) {
			ChatOfflinePlayer player = manager.toChatOfflinePlayer(profile);
			MutedPlayer mute = player.getMute(false);
			ChatTextComponent component = new ChatTextComponent();
			component.append(new ChatTextComponent(player.getName()));
			if (mute != null && !mute.isExpired(now)) {
				ModeratorType moderatorType = mute.getModeratorType();
				switch (moderatorType) {
					case PLAYER:
					case SERVER:
					case PLUGIN:
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
							default: throw new RuntimeException();
						}
						String reason = mute.getReason();
						if (reason != null) {
							component.append(new ChatTextComponent("\nПричина: "));
							component.append(new ChatTextComponent(reason));
						}
						break;
					case AUTOMOD:
						component.append(new ChatTextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
						break;
					case UNKNOWN:
					default:
						component.append(new ChatTextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
						break;
				}
				Date expiration = mute.getExpirationAt();
				if (expiration != null) {
					component.append(new ChatTextComponent("\nЗаглушён временно, до: " + expiration.toString()));
				} else {
					component.append(new ChatTextComponent("\nЗаглушён навсегда"));
				}
				stack.sendSuccess(ForgeParser.forge(component), false);
				i++;
			} else {
				component.append(new ChatTextComponent(" не заглушён"));
				stack.sendSuccess(ForgeParser.forge(component), false);
			}
		}
		return i;
	}
}