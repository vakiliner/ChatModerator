package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class FabricChatPlayer extends FabricChatOfflinePlayer implements ChatPlayer {
	protected final ServerPlayer player;

	public FabricChatPlayer(FabricParser parser, ServerPlayer player) {
		super(parser, player.server, player.getGameProfile());
		this.player = Objects.requireNonNull(player);
	}

	public ServerPlayer getPlayer() {
		return this.player;
	}

	public ChatServer getServer() {
		return this.parser.toChatServer(this.player.server);
	}

	public ChatComponent getDisplayName() {
		return FabricParser.fabric(this.player.getDisplayName());
	}

	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.player.gameMode.getGameModeForPlayer().getId());
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.player, component, type, uuid);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatPlayer other = (FabricChatPlayer) obj;
			return this.parser.equals(other.parser) && this.player.equals(other.player);
		} else {
			return false;
		}
	}
}