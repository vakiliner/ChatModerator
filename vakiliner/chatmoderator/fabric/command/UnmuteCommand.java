package vakiliner.chatmoderator.fabric.command;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TextComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

public class UnmuteCommand {
	public static final SimpleCommandExceptionType ERROR_NOT_MUTED = new SimpleCommandExceptionType(new TextComponent("This player is not muted"));

	public static LiteralArgumentBuilder<CommandSourceStack> register(FabricChatModerator manager, CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> unmute = LiteralArgumentBuilder.literal("unmute");
		return unmute.requires((stack) -> {
			return stack.hasPermission(3);
		}).then(Commands.argument("target", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
			Date now = new Date();
			return SharedSuggestionProvider.suggest(ChatModeratorModInitializer.MANAGER.mutes.map().values().stream().filter((mute) -> !mute.isExpired(now)).map(MutedPlayer::getName).collect(Collectors.toList()), builder);
		}).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			return unmutePlayer(context.getSource(), collection);
		}));
	}

	private static int unmutePlayer(CommandSourceStack stack, Collection<GameProfile> collection) throws CommandSyntaxException {
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		int i = 0;
		for (GameProfile profile : collection) {
			if (manager.mutes.unmute(profile.getId())) {
				ChatTextComponent component = new ChatTextComponent(profile.getName() + " теперь снова может общаться");
				stack.sendSuccess(FabricParser.fabric(component), true);
				i++;
			}
		}
		if (i == 0) {
			throw ERROR_NOT_MUTED.create();
		} else {
			return i;
		}
	}
}