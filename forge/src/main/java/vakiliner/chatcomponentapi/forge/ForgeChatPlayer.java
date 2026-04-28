package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatPlayer extends ForgeChatOfflinePlayer implements ChatPlayer {
	protected final ServerPlayerEntity player;

	public ForgeChatPlayer(ForgeParser parser, ServerPlayerEntity player) {
		super(parser, player.getServer(), player.getGameProfile());
		this.player = Objects.requireNonNull(player);
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public ChatComponent getDisplayName() {
		return ForgeParser.forge(this.player.getDisplayName());
	}

	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.player.gameMode.getGameModeForPlayer().getId());
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.player, component, type, uuid);
	}
}