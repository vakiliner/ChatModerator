package vakiliner.chatmoderator.bukkit;

import java.util.Iterator;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BukkitListener implements Listener {
	private final BukkitChatModerator manager;

	protected BukkitListener(BukkitChatModerator manager) {
		this.manager = manager;
	}

	@EventHandler(ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		this.manager.onChat(this.manager.toChatPlayer(player), event.getMessage(), () -> event.setCancelled(true), () -> {
			Iterator<Player> iterator = event.getRecipients().iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getGameMode() != GameMode.SPECTATOR) iterator.remove();
			}
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		this.manager.onChat(this.manager.toChatPlayer(event.getPlayer()), event.getMessage(), () -> event.setCancelled(true), null);
	}
}