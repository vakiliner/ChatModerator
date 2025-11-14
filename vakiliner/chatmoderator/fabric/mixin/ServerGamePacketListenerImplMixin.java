package vakiliner.chatmoderator.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.ChatVisiblity;
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
		try {
			manager.onChat(manager.toChatPlayer(player), packet.getMessage(), callbackInfo::cancel);
		} catch (Throwable err) {
			FabricChatModerator.LOGGER.error(err);
		}
	}
}