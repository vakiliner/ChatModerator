package vakiliner.chatmoderator.bukkit;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.Server;
import com.google.common.collect.Collections2;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatPlayerList;
import vakiliner.chatmoderator.base.ChatServer;

public class BukkitChatServer extends vakiliner.chatcomponentapi.craftbukkit.BukkitChatServer implements ChatServer, ChatPlayerList {
	private final BukkitChatModerator manager;

	public BukkitChatServer(BukkitChatModerator manager, Server server) {
		super(BukkitChatModerator.PARSER, server);
		this.manager = manager;
	}

	@Override
	public ChatServer getServer() {
		return this;
	}

	@Override
	public ChatPlayerList getPlayerList() {
		return this;
	}

	@Override
	public Collection<? extends ChatPlayer> getPlayers() {
		return Collections2.transform(this.server.getOnlinePlayers(), this.manager::toChatPlayer);
	}

	@Override
	public ChatPlayer getPlayer(UUID uuid) {
		return this.manager.toChatPlayer(this.server.getPlayer(uuid));
	}

	@Override
	public ChatPlayer getPlayer(String name) {
		return this.manager.toChatPlayer(this.server.getPlayerExact(name));
	}
}
