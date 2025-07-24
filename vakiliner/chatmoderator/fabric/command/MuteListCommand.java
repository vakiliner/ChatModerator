package vakiliner.chatmoderator.fabric.command;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

public class MuteListCommand {
	public static LiteralArgumentBuilder<CommandSourceStack> register(FabricChatModerator manager, CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> mutes = LiteralArgumentBuilder.literal("mutes");
		LiteralArgumentBuilder<CommandSourceStack> get = LiteralArgumentBuilder.literal("get");
		return mutes.requires((stack) -> {
			return stack.hasPermission(3);
		}).executes((context) -> {
			return listMutes(context.getSource(), 1);
		}).then(get.then(Commands.argument("target", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
			return SharedSuggestionProvider.suggest(manager.mutes.map().values().stream().map(MutedPlayer::getName).collect(Collectors.toList()), builder);
		}).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			return getMute(context.getSource(), collection.iterator().next());
		}))).then(Commands.argument("page", IntegerArgumentType.integer(0)).executes((context) -> {
			int page = IntegerArgumentType.getInteger(context, "page");
			return listMutes(context.getSource(), page);
		}));
	}

	private static int listMutes(CommandSourceStack stack, int page) throws CommandSyntaxException {
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		Date now = new Date();
		List<MutedPlayer> mutes = manager.mutes.map().values().stream().filter((mute) -> !mute.isExpired(now)).collect(Collectors.toList());
		int size = mutes.size();
		int pages = (size - 1) / 10 + 1;
		if (page < 1) {
			page = pages;
		} else if (page > pages) {
			page = 1;
		}
		MutableComponent border = TextComponent.EMPTY.copy();
		border.append(button("[⏮]", 1, "First page"));
		border.append(new TextComponent(" "));
		border.append(button("[⏪]", page - 1, "Previous page"));
		border.append(new TextComponent(" "));
		border.append(new TranslatableComponent("book.pageIndicator", new TextComponent(Integer.toString(page)), new TextComponent(Integer.toString(pages))));
		border.append(new TextComponent(" "));
		border.append(button("[⏩]", page + 1, "Next page"));
		border.append(new TextComponent(" "));
		border.append(button("[⏭]", pages, "Last page"));
		stack.sendSuccess(border, false);
		if (!mutes.isEmpty()) {
			int a = page * 10;
			for (int i = (page - 1) * 10; a > i && size > i; i++) {
				MutedPlayer mute = mutes.get(i);
				MutableComponent component = TextComponent.EMPTY.copy();
				MutableComponent unmute = new TextComponent("[❌]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unmute " + mute.getName())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Unmute player"))));
				component.append(unmute);
				component.append(new TextComponent(" " + mute.getName()));
				ModeratorType moderatorType = mute.getModeratorType();
				switch (moderatorType) {
					case PLAYER:
						component.append(new TextComponent(" заглушён модератором " + mute.getModeratorName()));
						break;
					case SERVER:
						component.append(new TextComponent(" заглушён сервером"));
						break;
					case PLUGIN:
						component.append(new TextComponent(" заглушён плагином " + mute.getModeratorName()));
						break;
					case AUTOMOD:
						component.append(new TextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
						break;
					default:
						component.append(new TextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
						break;
				}
				stack.sendSuccess(component, false);
			}
		} else {
			stack.sendSuccess(new TextComponent("No mutes"), false);
		}
		stack.sendSuccess(border, false);
		return size;
	}

	private static MutableComponent button(String button, int page, String text) {
		return new TextComponent(button).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mutes " + page)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(text))));
	}

	private static int getMute(CommandSourceStack stack, GameProfile gameProfile) throws CommandSyntaxException {
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		if (gameProfile == null) {
			throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();
		}
		MutedPlayer mute = manager.mutes.get(gameProfile.getId());
		MutableComponent component = TextComponent.EMPTY.copy();
		component.append(new SelectorComponent(gameProfile.getName()));
		if (mute != null && !mute.isExpired()) {
			ModeratorType moderatorType = mute.getModeratorType();
			switch (moderatorType) {
				case PLAYER:
				case SERVER:
				case PLUGIN:
					switch (moderatorType) {
						case PLAYER:
							component.append(new TextComponent(" заглушён модератором " + mute.getModeratorName()));
							break;
						case SERVER:
							component.append(new TextComponent(" заглушён сервером"));
							break;
						case PLUGIN:
							component.append(new TextComponent(" заглушён плагином " + mute.getModeratorName()));
							break;
						default: throw new RuntimeException();
					}
					String reason = mute.getReason();
					if (reason != null) {
						component.append(new TextComponent("\nПричина: "));
						component.append(new TextComponent(reason));
					}
					break;
				case AUTOMOD:
					component.append(new TextComponent(" заглушён правилом автомодерации: " + mute.getModeratorName()));
					break;
				default:
					component.append(new TextComponent(" заглушён неизвестным источником: " + mute.getModeratorName()));
					break;
			}
			Date expiration = mute.getExpirationAt();
			if (expiration != null) {
				component.append(new TextComponent("\nЗаглушён временно, до: " + expiration.toString()));
			} else {
				component.append(new TextComponent("\nЗаглушён навсегда"));
			}
			stack.sendSuccess(component, false);
			return 1;
		} else {
			component.append(new TextComponent(" не заглушён"));
			stack.sendSuccess(component, false);
			return 0;
		}
	}
}