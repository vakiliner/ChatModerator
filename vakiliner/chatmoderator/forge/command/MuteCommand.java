package vakiliner.chatmoderator.forge.command;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;
import vakiliner.chatmoderator.forge.ChatModeratorModInitializer;
import vakiliner.chatmoderator.forge.ForgeChatModerator;

public class MuteCommand {
	public static final SimpleCommandExceptionType PLAYER_BYPASS_MUTES = new SimpleCommandExceptionType(new StringTextComponent("Cannot mute a player who can bypass mutes"));
	public static final SimpleCommandExceptionType ERROR_ALREADY_MUTED = new SimpleCommandExceptionType(new StringTextComponent("This player is already muted"));

	public static LiteralArgumentBuilder<CommandSource> register(ForgeChatModerator manager, CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> mute = LiteralArgumentBuilder.literal("mute");
		return mute.requires((stack) -> {
			return stack.hasPermission(3);
		}).then(Commands.argument("target", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
			return ISuggestionProvider.suggest(manager.getOnlinePlayers().stream().filter((player) -> !player.isMuted()).map(ChatPlayer::getName).collect(Collectors.toList()), builder);
		}).then(Commands.argument("duration", new ArgumentType<Integer>() {
			public Integer parse(StringReader reader) throws CommandSyntaxException {
				int start = reader.getCursor();
				String string = reader.readUnquotedString();
				if (string.equals("infinite")) {
					return 0;
				}
				if (string.isEmpty()) {
					reader.setCursor(start);
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(reader);
				}
				double duration;
				try {
					duration = Double.parseDouble(string);
				} catch (NumberFormatException err) {
					duration = 0;
				}
				if (duration <= 0 || duration * 10 % 1 != 0) {
					reader.setCursor(start);
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(reader, string);
				}
				return (int) (duration * 60);
			}
		}).suggests((context, builder) -> {
			return ISuggestionProvider.suggest(Collections.singleton("infinite"), builder);
		}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			Integer duration = IntegerArgumentType.getInteger(context, "duration");
			String reason = StringArgumentType.getString(context, "reason");
			if (duration == 0) duration = null;
			return mutePlayer(context.getSource(), collection, duration, reason);
		})).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			Integer duration = IntegerArgumentType.getInteger(context, "duration");
			if (duration == 0) duration = null;
			return mutePlayer(context.getSource(), collection, duration, null);
		})));
	}

	private static int mutePlayer(CommandSource stack, Collection<GameProfile> collection, Integer duration, String reason) throws CommandSyntaxException {
		ForgeChatModerator manager = ChatModeratorModInitializer.MANAGER;
		ChatOfflinePlayer player = manager.toChatOfflinePlayer(collection.iterator().next());
		if (player == null) {
			throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();
		}
		if (player.isBypassMutes()) {
			throw PLAYER_BYPASS_MUTES.create();
		}
		Entity entity = stack.getEntity();
		final ModeratorType moderatorType;
		if (entity == null) {
			moderatorType = ModeratorType.SERVER;
		} else if (entity instanceof ServerPlayerEntity) {
			moderatorType = ModeratorType.PLAYER;
		} else {
			moderatorType = ModeratorType.UNKNOWN;
		}
		if (manager.mutes.mute(player, entity != null ? entity.getName().getString() : "CONSOLE", moderatorType, duration, reason)) {
			ChatTextComponent component = new ChatTextComponent();
			component.append(ChatTextComponent.selector(player));
			component.append(new ChatTextComponent(" заглушён"));
			stack.sendSuccess(ForgeParser.forge(component), true);
			return 1;
		} else {
			throw ERROR_ALREADY_MUTED.create();
		}
	}
}