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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.SelectorTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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
			return ISuggestionProvider.suggest(manager.mutes.map().values().stream().map(MutedPlayer::getName).collect(Collectors.toList()), builder);
		}).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			return getMute(context.getSource(), collection.iterator().next());
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
		IFormattableTextComponent border = StringTextComponent.EMPTY.copy();
		border.append(button("[⏮]", 1, "First page"));
		border.append(new StringTextComponent(" "));
		border.append(button("[⏪]", page - 1, "Previous page"));
		border.append(new StringTextComponent(" "));
		border.append(new TranslationTextComponent("book.pageIndicator", new StringTextComponent(Integer.toString(page)), new StringTextComponent(Integer.toString(pages))));
		border.append(new StringTextComponent(" "));
		border.append(button("[⏩]", page + 1, "Next page"));
		border.append(new StringTextComponent(" "));
		border.append(button("[⏭]", pages, "Last page"));
		stack.sendSuccess(border, false);
		if (!mutes.isEmpty()) {
			int a = page * 10;
			for (int i = (page - 1) * 10; a > i && size > i; i++) {
				MutedPlayer mute = mutes.get(i);
				IFormattableTextComponent component = StringTextComponent.EMPTY.copy();
				IFormattableTextComponent unmute = new StringTextComponent("[❌]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unmute " + mute.getName())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Unmute player"))));
				component.append(unmute);
				component.append(new StringTextComponent(" " + mute.getName()));
				ModeratorType moderatorType = mute.getModeratorType();
				switch (moderatorType) {
					case PLAYER:
						component.append(new StringTextComponent(" заглушён модератором " + mute.getModeratorName()));
						break;
					case SERVER:
						component.append(new StringTextComponent(" заглушён сервером"));
						break;
					case PLUGIN:
						component.append(new StringTextComponent(" заглушён плагином " + mute.getModeratorName()));
						break;
					case AUTOMOD:
						component.append(new StringTextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
						break;
					default:
						component.append(new StringTextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
						break;
				}
				stack.sendSuccess(component, false);
			}
		} else {
			stack.sendSuccess(new StringTextComponent("No mutes"), false);
		}
		stack.sendSuccess(border, false);
		return size;
	}

	private static IFormattableTextComponent button(String button, int page, String text) {
		return new StringTextComponent(button).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutes " + page)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(text))));
	}

	private static int getMute(CommandSource stack, GameProfile gameProfile) throws CommandSyntaxException {
		ForgeChatModerator manager = ChatModeratorModInitializer.MANAGER;
		if (gameProfile == null) {
			throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();
		}
		MutedPlayer mute = manager.mutes.get(gameProfile.getId());
		IFormattableTextComponent component = StringTextComponent.EMPTY.copy();
		component.append(new SelectorTextComponent(gameProfile.getName()));
		if (mute != null && !mute.isExpired()) {
			ModeratorType moderatorType = mute.getModeratorType();
			switch (moderatorType) {
				case PLAYER:
				case SERVER:
				case PLUGIN:
					switch (moderatorType) {
						case PLAYER:
							component.append(new StringTextComponent(" заглушён модератором " + mute.getModeratorName()));
							break;
						case SERVER:
							component.append(new StringTextComponent(" заглушён сервером"));
							break;
						case PLUGIN:
							component.append(new StringTextComponent(" заглушён плагином " + mute.getModeratorName()));
							break;
						default: throw new RuntimeException();
					}
					String reason = mute.getReason();
					if (reason != null) {
						component.append(new StringTextComponent("\nПричина: "));
						component.append(new StringTextComponent(reason));
					}
					break;
				case AUTOMOD:
					component.append(new StringTextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
					break;
				default:
					component.append(new StringTextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
					break;
			}
			Date expiration = mute.getExpirationAt();
			if (expiration != null) {
				component.append(new StringTextComponent("\nЗаглушён временно, до: " + expiration.toString()));
			} else {
				component.append(new StringTextComponent("\nЗаглушён навсегда"));
			}
			stack.sendSuccess(component, false);
			return 1;
		} else {
			component.append(new StringTextComponent(" не заглушён"));
			stack.sendSuccess(component, false);
			return 0;
		}
	}
}