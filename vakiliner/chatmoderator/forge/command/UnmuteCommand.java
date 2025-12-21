package vakiliner.chatmoderator.forge.command;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.util.text.StringTextComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.forge.ChatModeratorModInitializer;
import vakiliner.chatmoderator.forge.ForgeChatModerator;

public class UnmuteCommand {
	public static final SimpleCommandExceptionType ERROR_NOT_MUTED = new SimpleCommandExceptionType(new StringTextComponent("This player is not muted"));

	public static LiteralArgumentBuilder<CommandSource> register(ForgeChatModerator manager, CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> unmute = LiteralArgumentBuilder.literal("unmute");
		return unmute.requires((stack) -> {
			return stack.hasPermission(3);
		}).then(Commands.argument("target", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
			Date now = new Date();
			return ISuggestionProvider.suggest(manager.mutes.map().values().stream().filter((mute) -> !mute.isExpired(now)).map(MutedPlayer::getName).collect(Collectors.toList()), builder);
		}).executes((context) -> {
			Collection<GameProfile> collection = GameProfileArgument.getGameProfiles(context, "target");
			return unmutePlayer(context.getSource(), collection);
		}));
	}

	private static int unmutePlayer(CommandSource stack, Collection<GameProfile> collection) throws CommandSyntaxException {
		ForgeChatModerator manager = ChatModeratorModInitializer.MANAGER;
		int i = 0;
		for (GameProfile profile : collection) {
			if (manager.mutes.unmute(profile.getId())) {
				ChatTextComponent component = new ChatTextComponent(profile.getName() + " теперь снова может общаться");
				stack.sendSuccess(ForgeParser.forge(component), true);
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