package vakiliner.chatmoderator.bukkit;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.Server;
import vakiliner.chatcomponentapi.util.ParseCollection;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatPlayerList;
import vakiliner.chatmoderator.base.ChatServer;

public class BukkitChatServer extends vakiliner.chatcomponentapi.craftbukkit.BukkitChatServer implements ChatServer, ChatPlayerList {
	private final BukkitChatModerator manager;

	public BukkitChatServer(BukkitChatModerator manager, Server server) {
		super(BukkitChatModerator.PARSER, server);
		this.manager = manager;
	}

	public ChatServer getServer() {
		return this;
	}

	public ChatPlayerList getPlayerList() {
		return this;
	}

	public Collection<? extends ChatPlayer> getPlayers() {
		return new ParseCollection<>(this.server.getOnlinePlayers(), this.manager::toChatPlayer);
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.manager.toChatPlayer(this.server.getPlayer(uuid));
	}

	public ChatPlayer getPlayer(String name) {
		return this.manager.toChatPlayer(this.server.getPlayerExact(name));
	}
}