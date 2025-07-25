package vakiliner.chatmoderator.bukkit;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {
	private final BukkitChatModerator manager;

	protected BukkitListener(BukkitChatModerator manager) {
		this.manager = manager;
	}

	@EventHandler(ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		this.manager.onChat(this.manager.toChatPlayer(event.getPlayer()), event.getMessage(), () -> event.setCancelled(true));
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		this.manager.onChat(this.manager.toChatPlayer(event.getPlayer()), event.getMessage(), () -> event.setCancelled(true));
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.addAttachment(this.manager.getPlugin(), BukkitChatModerator.SPECTATORS_CHAT, player.getGameMode() == GameMode.SPECTATOR);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		GameMode gameMode = event.getNewGameMode();
		if ((player.getGameMode() == GameMode.SPECTATOR) != (gameMode == GameMode.SPECTATOR)) {
			player.addAttachment(this.manager.getPlugin(), BukkitChatModerator.SPECTATORS_CHAT, gameMode == GameMode.SPECTATOR);
		}
	}
}