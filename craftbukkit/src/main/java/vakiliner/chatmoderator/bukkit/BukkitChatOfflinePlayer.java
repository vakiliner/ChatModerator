package vakiliner.chatmoderator.bukkit;

import java.util.Date;
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

	@Override
	public MutedPlayer getMute(Date filterExpired) {
		return this.manager.mutes.get(this.getUniqueId(), filterExpired);
	}

	@Override
	public boolean isBypassModeration() {
		Permission perm = Bukkit.getServer().getPluginManager().getPermission("chatmoderator.bypass_moderation.*");
		return perm != null ? perm.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
	}

	@Override
	public boolean isBypassMutes() {
		Permission perm = Bukkit.getServer().getPluginManager().getPermission("chatmoderator.bypass_moderation.mutes");
		return perm != null ? perm.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
	}
}
