package vakiliner.chatmoderator.bukkit;

import java.util.ArrayList;
import java.util.Collection;
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
			Collection<Player> recipients = new ArrayList<>();
			for (Player recipient : event.getRecipients()) if (recipient.getGameMode() == GameMode.SPECTATOR) recipients.add(recipient);
			event.getRecipients().clear();
			event.getRecipients().addAll(recipients);
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		this.manager.onChat(this.manager.toChatPlayer(event.getPlayer()), event.getMessage(), () -> event.setCancelled(true), null);
	}
}