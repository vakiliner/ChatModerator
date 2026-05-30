package vakiliner.chatmoderator.forge.command;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
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
			Date now = new Date();
			return ISuggestionProvider.suggest(context.getSource().getServer().getPlayerList().getPlayers().stream().filter((player) -> manager.mutes.get(player.getUUID(), now) == null).map((player) -> player.getGameProfile().getName()).collect(Collectors.toList()), builder);
		}).then(Commands.argument("minutes", DoubleArgumentType.doubleArg(0.1)).then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			double minutes = DoubleArgumentType.getDouble(context, "minutes");
			String reason = StringArgumentType.getString(context, "reason");
			return mutePlayer(context.getSource(), collection, minutes, reason);
		})).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			double minutes = DoubleArgumentType.getDouble(context, "minutes");
			return mutePlayer(context.getSource(), collection, minutes, null);
		})).then(Commands.literal("infinite").then(Commands.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			String reason = StringArgumentType.getString(context, "reason");
			return mutePlayer(context.getSource(), collection, 0, reason);
		})).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			return mutePlayer(context.getSource(), collection, 0, null);
		})));
	}

	private static int mutePlayer(CommandSource stack, Collection<GameProfile> collection, double minutes, String reason) throws CommandSyntaxException {
		ForgeChatModerator manager = ChatModeratorModInitializer.MANAGER;
		MinecraftServer server = stack.getServer();
		Entity entity = stack.getEntity();
		final ModeratorType moderatorType;
		if (entity == null) {
			moderatorType = ModeratorType.SERVER;
		} else if (entity instanceof ServerPlayerEntity) {
			moderatorType = ModeratorType.PLAYER;
		} else {
			moderatorType = ModeratorType.UNKNOWN;
		}
		boolean bypassMutes = true;
		int i = 0;
		for (GameProfile profile : collection) {
			ChatOfflinePlayer player = manager.toChatOfflinePlayer(server, profile);
			if (player.isBypassMutes()) {
				continue;
			} else {
				bypassMutes = false;
			}
			if (manager.mutes.mute(player, stack.getTextName(), moderatorType, minutes == 0 ? null : (int) (minutes * 60), reason)) {
				stack.sendSuccess(ForgeParser.forge(new ChatTextComponent(player.getName() + " больше не может общаться")), true);
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