package vakiliner.chatmoderator.fabric.mixin;

import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.ChatVisiblity;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	@Accessor("player")
	public abstract ServerPlayer getPlayer();

	@Inject(at = @At("INVOKE"), method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", cancellable = true)
	void handleChat(ServerboundChatPacket packet, CallbackInfo callbackInfo) {
		ServerPlayer player = this.getPlayer();
		if (player.getChatVisibility() == ChatVisiblity.HIDDEN) return;
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		ChatPlayer chatPlayer = manager.toChatPlayer(player);
		String message = packet.getMessage();
		try {
			manager.onChat(chatPlayer, message, callbackInfo::cancel, () -> {
				callbackInfo.cancel();
				player.getLevel().getServer().execute(() -> {
					ChatTranslateComponent component = new ChatTranslateComponent("<%s> %s", "chat.type.text", chatPlayer.getDisplayName(), new ChatTextComponent(message));
					Set<ChatCommandSender> recipients = new HashSet<>();
					MinecraftServer server = manager.getServer();
					recipients.add(manager.toChatCommandSender(server));
					for (ServerPlayer recipient : server.getPlayerList().getPlayers()) {
						if (recipient.isSpectator()) {
							recipients.add(manager.toChatPlayer(recipient));
						}
					}
					for (ChatCommandSender recipient : recipients) {
						recipient.sendMessage(component);
					}
				});
			});
		} catch (Throwable err) {
			FabricChatModerator.LOGGER.error("Failed to handle chat", err);
		}
	}
}