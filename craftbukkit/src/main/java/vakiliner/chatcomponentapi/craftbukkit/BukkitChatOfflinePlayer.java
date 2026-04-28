package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;

public class BukkitChatOfflinePlayer implements ChatOfflinePlayer {
	protected final BukkitParser parser;
	protected final OfflinePlayer player;

	public BukkitChatOfflinePlayer(BukkitParser parser, OfflinePlayer player) {
		this.parser = Objects.requireNonNull(parser);
		this.player = Objects.requireNonNull(player);
	}

	public OfflinePlayer getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.player.getName();
	}

	public UUID getUniqueId() {
		return this.player.getUniqueId();
	}

	public boolean isOp() {
		return this.player.isOp();
	}

	public ChatTeam getTeam() {
		return this.parser.toChatTeam(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(this.getName()));
	}
}