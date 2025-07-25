package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

public class FabricChatPlayer implements ChatPlayer {
	protected final FabricParser parser;
	protected final ServerPlayer player;

	public FabricChatPlayer(FabricParser parser, ServerPlayer player) {
		this.parser = Objects.requireNonNull(parser);
		this.player = Objects.requireNonNull(player);
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.player.getGameProfile().getName();
	}

	public UUID getUniqueId() {
		return this.player.getGameProfile().getId();
	}

	public boolean isOp() {
		return this.player.getServer().getPlayerList().isOp(this.player.getGameProfile());
	}

	public ChatTeam getTeam() {
		return this.parser.toChatTeam(this.player.getServer().getScoreboard().getPlayerTeam(this.getName()));
	}

	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.player.gameMode.getGameModeForPlayer().getId());
	}

	public void sendMessage(String message) {
		this.sendMessage(Util.NIL_UUID, message);
	}

	public void sendMessage(ChatComponent component) {
		this.sendMessage(Util.NIL_UUID, component);
	}

	public void sendMessage(UUID uuid, String message) {
		this.sendMessage(uuid, new ChatTextComponent(message));
	}

	public void sendMessage(UUID uuid, ChatComponent component) {
		this.player.sendMessage(FabricParser.fabric(component), uuid != Util.NIL_UUID ? ChatType.CHAT : ChatType.SYSTEM, uuid);
	}
}