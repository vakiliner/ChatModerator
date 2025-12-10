package vakiliner.chatmoderator.fabric.mixin;

import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	private static final String A = "/all ";

	@Accessor("player")
	public abstract ServerPlayer getPlayer();

	@Invoker("handleChat")
	public abstract void handleChat(ServerboundChatPacket packet);

	@Inject(at = @At("INVOKE"), method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", cancellable = true)
	void handleChat(ServerboundChatPacket packet, CallbackInfo callbackInfo) {
		if (packet instanceof HandledPacket) return;
		String message = packet.getMessage();
		if (message.startsWith("/")) {
			if (!message.startsWith(A)) return;
			message = message.replaceFirst(A, A + "-c ");
		} else {
			message = A + "-m " + message;
		}
		this.handleChat(new HandledPacket(message));
		callbackInfo.cancel();
	}

	@Inject(at = @At("INVOKE"), method = "handleCommand", cancellable = true)
	void handleCommand(String command, CallbackInfo callbackInfo) {
		if (command.startsWith(A)) {
			int i = command.indexOf(' ', A.length());
			String arg = command.substring(A.length(), i);
			String message = command.substring(i + 1);
			switch (arg) {
				case "-m":
					command = message;
					break;
				case "-c":
					command = A + message;
					break;
				default: throw new IllegalArgumentException();
			}
		}
		FabricChatModerator manager = ChatModeratorModInitializer.MANAGER;
		ChatPlayer player = manager.toChatPlayer(this.getPlayer());
		String message = command;
		try {
			manager.onChat(player, message, callbackInfo::cancel, () -> {
				callbackInfo.cancel();
				ChatTranslateComponent component = new ChatTranslateComponent("<%s> %s", "chat.type.text", player.getDisplayName(), new ChatTextComponent(message));
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
		} catch (Throwable err) {
			FabricChatModerator.LOGGER.error("Failed to handle chat", err);
			return;
		}
		if (message.startsWith("/") || callbackInfo.isCancelled()) return;
		callbackInfo.cancel();
		ChatComponent chatComponent = new ChatTranslateComponent("<%s> %s", "chat.type.text", player.getDisplayName(), new ChatTextComponent(message));
		manager.getServer().getPlayerList().broadcastMessage(FabricParser.fabric(chatComponent), ChatType.CHAT, player.getUniqueId());
	}

	private static class HandledPacket extends ServerboundChatPacket {
		public HandledPacket(String message) {
			super(message);
		}
	}
}