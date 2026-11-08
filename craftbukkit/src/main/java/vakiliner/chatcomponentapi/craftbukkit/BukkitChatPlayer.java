package vakiliner.chatcomponentapi.craftbukkit;

import java.util.UUID;
import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

public class BukkitChatPlayer extends BukkitChatOfflinePlayer implements ChatPlayer {
	public BukkitChatPlayer(BukkitParser parser, Player player) {
		super(parser, player);
	}

	@Override
	public Player getPlayer() {
		return (Player) super.getPlayer();
	}

	@Override
	public ChatServer getServer() {
		return this.parser.toChatServer(this.getPlayer().getServer());
	}

	@Override
	public ChatComponent getDisplayName() {
		return new ChatTextComponent(this.getPlayer().getDisplayName());
	}

	@SuppressWarnings("deprecation")
	@Override
	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.getPlayer().getGameMode().getValue());
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.getPlayer(), component, type, uuid);
	}
}
