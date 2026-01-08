package vakiliner.chatmoderator.fabric.mixin;

import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.fabric.ChatModeratorModInitializer;
import vakiliner.chatmoderator.fabric.FabricChatModerator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	private static final String A = "/all ";

	@Accessor("server")
	public abstract MinecraftServer getServer();

	@Accessor("player")
	public abstract ServerPlayer getPlayer();

	@ModifyVariable(at = @At("HEAD"), method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", argsOnly = true)
	ServerboundChatPacket handleChat(ServerboundChatPacket packet) {
		if (packet instanceof HandledPacket) return packet;
		String message = packet.getMessage();
		if (message.startsWith("/")) {
			if (!message.startsWith(A)) return new HandledPacket(message);
			message = message.replaceFirst(A, A + "-c ");
		} else {
			message = A + "-m " + message;
		}
		return new HandledPacket(message);
	}

	@Inject(at = @At("HEAD"), method = "handleCommand", cancellable = true)
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
				MinecraftServer server = this.getServer();
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
		FabricChatModerator.PARSER.broadcastMessage(this.getServer().getPlayerList(), chatComponent, ChatMessageType.CHAT, player.getUniqueId());
	}

	private static class HandledPacket extends ServerboundChatPacket {
		public HandledPacket(String message) {
			super(message);
		}
	}
}