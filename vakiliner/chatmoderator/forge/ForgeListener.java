package vakiliner.chatmoderator.forge;

import java.util.HashSet;
import java.util.Set;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.forge.command.MuteCommand;
import vakiliner.chatmoderator.forge.command.MuteListCommand;
import vakiliner.chatmoderator.forge.command.UnmuteCommand;

@EventBusSubscriber(modid = ForgeChatModerator.ID)
class ForgeListener {
	private final ForgeChatModerator manager;

	public ForgeListener(ForgeChatModerator manager) {
		this.manager = manager;
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		this.manager.server = event.getServer();
	}

	@SubscribeEvent
	public void onServerStopped(FMLServerStoppedEvent event) {
		if (this.manager.server == event.getServer()) {
			this.manager.server = null;
		}
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		dispatcher.register(MuteCommand.register(this.manager, dispatcher));
		dispatcher.register(UnmuteCommand.register(this.manager, dispatcher));
		dispatcher.register(MuteListCommand.register(this.manager, dispatcher));
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		ChatPlayer player = this.manager.toChatPlayer(event.getPlayer());
		String message = event.getMessage();
		this.manager.onChat(player, event.getMessage(), () -> event.setCanceled(true), () -> {
			event.setCanceled(true);
			ChatTranslateComponent component = new ChatTranslateComponent("<%s> %s", "chat.type.text", player.getDisplayName(), new ChatTextComponent(message));
			Set<ChatCommandSender> recipients = new HashSet<>();
			recipients.add(this.manager.toChatCommandSender(this.manager.server));
			for (ServerPlayerEntity recipient : this.manager.server.getPlayerList().getPlayers()) {
				if (recipient.isSpectator()) {
					recipients.add(this.manager.toChatPlayer(recipient));
				}
			}
			for (ChatCommandSender recipient : recipients) {
				recipient.sendMessage(component);
			}
		});
	}

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		ParseResults<CommandSource> parseResults = event.getParseResults();
		Entity entity = parseResults.getContext().getSource().getEntity();
		if (entity instanceof ServerPlayerEntity) {
			this.manager.onChat(this.manager.toChatPlayer((ServerPlayerEntity) entity), parseResults.getReader().getRead(), () -> event.setCanceled(true), null);
		}
	}
}