package vakiliner.chatcomponentapi.craftbukkit;

import java.util.UUID;
import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.common.ChatGameMode;
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

	public void sendMessage(String message) {
		this.getPlayer().sendMessage(message);
	}

	public void sendMessage(UUID uuid, String message) {
		this.getPlayer().sendMessage(uuid, message);
	}

	public void sendMessage(ChatComponent component) {
		this.parser.sendMessage(this.getPlayer(), component);
	}

	public void sendMessage(UUID uuid, ChatComponent component) {
		this.parser.sendMessage(this.getPlayer(), uuid, component);
	}
}