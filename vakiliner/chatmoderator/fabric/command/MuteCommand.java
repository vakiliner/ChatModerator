package vakiliner.chatmoderator.fabric.command;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
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
		}).then(Commands.argument("duration", StringArgumentType.string()).suggests((context, builder) -> {
			return SharedSuggestionProvider.suggest(Collections.singleton("infinite"), builder);
		}).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			String duration = StringArgumentType.getString(context, "duration");
			String reason = StringArgumentType.getString(context, "reason");
			return mutePlayer(context.getSource(), collection, duration, reason);
		})).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			String duration = StringArgumentType.getString(context, "duration");
			return mutePlayer(context.getSource(), collection, duration, null);
		})));
	}

	private static int mutePlayer(CommandSourceStack stack, Collection<GameProfile> collection, String rawDuration, String reason) throws CommandSyntaxException {
		final Integer duration;
		if (rawDuration.equals("infinite")) {
			duration = null;
		} else {
			double a;
			try {
				a = Double.parseDouble(rawDuration);
			} catch (NumberFormatException err) {
				a = 0;
			}
			if (a <= 0 || a * 10 % 1 != 0) {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(rawDuration);
			}
			duration = (int) (a * 60);
		}
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
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
		} else if (entity instanceof ServerPlayer) {
			moderatorType = ModeratorType.PLAYER;
		} else {
			moderatorType = ModeratorType.UNKNOWN;
		}
		if (manager.mutes.mute(player, entity != null ? entity.getName().getString() : "CONSOLE", moderatorType, duration, reason)) {
			ChatTextComponent component = new ChatTextComponent();
			component.append(ChatTextComponent.selector(player));
			component.append(new ChatTextComponent(" заглушён"));
			stack.sendSuccess(FabricParser.fabric(component), true);
			return 1;
		} else {
			throw ERROR_ALREADY_MUTED.create();
		}
	}
}