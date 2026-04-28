package vakiliner.chatmoderator.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class BukkitChatOfflinePlayer extends vakiliner.chatcomponentapi.craftbukkit.BukkitChatOfflinePlayer implements ChatOfflinePlayer {
	private final BukkitChatModerator manager;

	public BukkitChatOfflinePlayer(BukkitChatModerator manager, OfflinePlayer player) {
		super(BukkitChatModerator.PARSER, player);
		this.manager = manager;
	}

	public MutedPlayer getMute(boolean ignoreExpired) {
		MutedPlayer mute = this.manager.mutes.get(this.getUniqueId());
		if (mute != null && !(ignoreExpired && mute.isExpired())) {
			return mute;
		}
		return null;
	}

	public boolean isBypassModeration() {
		Permission perm = Bukkit.getServer().getPluginManager().getPermission("chatmoderator.bypass_moderation.*");
		return perm != null ? perm.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
	}

	public boolean isBypassMutes() {
		Permission perm = Bukkit.getServer().getPluginManager().getPermission("chatmoderator.bypass_moderation.mutes");
		return perm != null ? perm.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
	}
}