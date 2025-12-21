package vakiliner.chatmoderator.fabric.command;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

public class MuteCommand {
	public static final SimpleCommandExceptionType PLAYER_BYPASS_MUTES = new SimpleCommandExceptionType(new TextComponent("Cannot mute a player who can bypass mutes"));
	public static final SimpleCommandExceptionType ERROR_ALREADY_MUTED = new SimpleCommandExceptionType(new TextComponent("This player is already muted"));

	public static LiteralArgumentBuilder<CommandSourceStack> register(FabricChatModerator manager, CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> mute = LiteralArgumentBuilder.literal("mute");
		return mute.requires((stack) -> {
			return stack.hasPermission(3);
		}).then(Commands.argument("target", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
			return SharedSuggestionProvider.suggest(ChatModeratorModInitializer.MANAGER.getOnlinePlayers().stream().filter((player) -> !player.isMuted()).map(ChatPlayer::getName).collect(Collectors.toList()), builder);
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
			return SharedSuggestionProvider.suggest(Collections.singleton("infinite"), builder);
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

	private static int mutePlayer(CommandSourceStack stack, Collection<GameProfile> collection, Integer duration, String reason) throws CommandSyntaxException {
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		Entity entity = stack.getEntity();
		final ModeratorType moderatorType;
		if (entity == null) {
			moderatorType = ModeratorType.SERVER;
		} else if (entity instanceof ServerPlayer) {
			moderatorType = ModeratorType.PLAYER;
		} else {
			moderatorType = ModeratorType.UNKNOWN;
		}
		boolean bypassMutes = true;
		int i = 0;
		for (GameProfile profile : collection) {
			ChatOfflinePlayer player = manager.toChatOfflinePlayer(profile);
			if (player.isBypassMutes()) {
				continue;
			} else {
				bypassMutes = false;
			}
			if (manager.mutes.mute(player, stack.getTextName(), moderatorType, duration, reason)) {
				ChatTextComponent component = new ChatTextComponent(player.getName() + " заглушён");
				stack.sendSuccess(FabricParser.fabric(component), true);
				i++;
			}
		}
		if (i == 0) {
			if (bypassMutes) {
				throw PLAYER_BYPASS_MUTES.create();
			} else {
				throw ERROR_ALREADY_MUTED.create();
			}
		} else {
			return i;
		}
	}
}