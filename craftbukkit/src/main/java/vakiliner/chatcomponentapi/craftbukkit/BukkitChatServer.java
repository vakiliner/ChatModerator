package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Server;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.util.ParseCollection;

public class BukkitChatServer implements ChatServer, ChatPlayerList {
	protected final BukkitParser parser;
	protected final Server server;

	public BukkitChatServer(BukkitParser parser, Server server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public Server getImpl() {
		return this.server;
	}

	public ChatServer getServer() {
		return this;
	}

	public ChatPlayerList getPlayerList() {
		return this;
	}

	public Collection<ChatPlayer> getPlayers() {
		return new ParseCollection<>(this.server.getOnlinePlayers(), this.parser::toChatPlayer);
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.server.getPlayer(uuid));
	}

	public ChatPlayer getPlayer(String name) {
		return this.parser.toChatPlayer(this.server.getPlayerExact(name));
	}

	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server.getScheduler(), plugin, runnable);
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.server, component, type, uuid);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatServer other = (BukkitChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}