package vakiliner.chatcomponentapi.craftbukkit;

import java.util.UUID;
import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitChatPlayer extends BukkitChatOfflinePlayer implements ChatPlayer {
	public BukkitChatPlayer(BukkitParser parser, Player player) {
		super(parser, player);
	}

	public Player getPlayer() {
		return (Player) super.getPlayer();
	}

	@SuppressWarnings("deprecation")
	public ChatGameMode getGameMode() {
		return ChatGameMode.getByValue(this.getPlayer().getGameMode().getValue());
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.getPlayer(), component, type, uuid);
	}
}