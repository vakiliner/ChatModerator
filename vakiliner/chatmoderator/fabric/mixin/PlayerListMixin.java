package vakiliner.chatmoderator.fabric.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Invoker("getPlayer")
	public abstract ServerPlayer getPlayer(UUID uUID);

	@Inject(at = @At("INVOKE"), method = "broadcastMessage", cancellable = true)
	void broadcastMessage(Component rawComponent, ChatType chatType, UUID uuid, CallbackInfo callbackInfo) {
		if (!(rawComponent instanceof TranslatableComponent)) return;
		TranslatableComponent component = (TranslatableComponent) rawComponent;
		if (!component.getKey().equals("chat.type.text") || component.getArgs().length != 2) return;
		ServerPlayer player = this.getPlayer(uuid);
		if (player == null) return;
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		ChatPlayer chatPlayer = manager.toChatPlayer(player);
		String message = (String) component.getArgs()[1];
		try {
			manager.onChat(chatPlayer, message, callbackInfo::cancel, () -> {
				callbackInfo.cancel();
				ChatComponent chatComponent = FabricParser.fabric(component);
				Set<ChatCommandSender> recipients = new HashSet<>();
				MinecraftServer server = manager.getServer();
				recipients.add(manager.toChatCommandSender(server));
				for (ServerPlayer recipient : server.getPlayerList().getPlayers()) {
					if (recipient.isSpectator()) {
						recipients.add(manager.toChatPlayer(recipient));
					}
				}
				for (ChatCommandSender recipient : recipients) {
					recipient.sendMessage(chatComponent);
				}
			});
		} catch (Throwable err) {
			FabricChatModerator.LOGGER.error("Failed to handle chat", err);
		}
	}
}