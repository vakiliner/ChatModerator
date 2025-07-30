package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatPlayer implements ChatPlayer {
	protected final ForgeParser parser;
	protected final ServerPlayerEntity player;

	public ForgeChatPlayer(ForgeParser parser, ServerPlayerEntity player) {
		this.parser = Objects.requireNonNull(parser);
		this.player = Objects.requireNonNull(player);
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.player.getGameProfile().getName();
	}

	public UUID getUniqueId() {
		return this.player.getGameProfile().getId();
	}

	@SuppressWarnings("null")
	public boolean isOp() {
		return this.player.getServer().getPlayerList().isOp(this.player.getGameProfile());
	}

	@SuppressWarnings("null")
	public ChatTeam getTeam() {
		return this.parser.toChatTeam(this.player.getServer().getScoreboard().getPlayerTeam(this.getName()));
	}

	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.player.gameMode.getGameModeForPlayer().getId());
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.player, component, type, uuid);
	}
}