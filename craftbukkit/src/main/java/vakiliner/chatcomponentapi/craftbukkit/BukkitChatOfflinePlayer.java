package vakiliner.chatcomponentapi.craftbukkit;

import static java.util.Objects.requireNonNull;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.mojang.authlib.GameProfile;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;

public class BukkitChatOfflinePlayer implements ChatOfflinePlayer {
	protected final BukkitParser parser;
	protected final OfflinePlayer player;

	public BukkitChatOfflinePlayer(BukkitParser parser, OfflinePlayer player) {
		this.parser = requireNonNull(parser);
		this.player = requireNonNull(player);
	}

	public OfflinePlayer getPlayer() {
		return this.player;
	}

	@Override
	public GameProfile getGameProfile() {
		return new GameProfile(this.player.getUniqueId(), this.player.getName());
	}

	@Override
	public String getName() {
		return this.player.getName();
	}

	@Override
	public UUID getUniqueId() {
		return this.player.getUniqueId();
	}

	@Override
	public boolean isOp() {
		return this.player.isOp();
	}

	@Override
	public ChatTeam getTeam() {
		return this.parser.toChatTeam(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(this.getName()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatOfflinePlayer other = (BukkitChatOfflinePlayer) obj;
			return this.parser.equals(other.parser) && this.player.equals(other.player);
		} else {
			return false;
		}
	}
}
