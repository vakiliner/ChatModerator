package vakiliner.chatcomponentapi.fabric;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.players.PlayerList;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.util.ParseCollection;

public class FabricChatPlayerList implements ChatPlayerList {
	protected final FabricParser parser;
	protected final PlayerList playerList;

	public FabricChatPlayerList(FabricParser parser, PlayerList playerList) {
		this.parser = Objects.requireNonNull(parser);
		this.playerList = Objects.requireNonNull(playerList);
	}

	public PlayerList getImpl() {
		return this.playerList;
	}

	public ChatServer getServer() {
		return this.parser.toChatServer(this.playerList.getServer());
	}

	public Collection<? extends ChatPlayer> getPlayers() {
		return new ParseCollection<>(this.playerList.getPlayers(), this.parser::toChatPlayer);
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.playerList.getPlayer(uuid));
	}

	public ChatPlayer getPlayer(String name) {
		return this.parser.toChatPlayer(this.playerList.getPlayerByName(name));
	}

	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.broadcastMessage(this.playerList, component, type, uuid);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatPlayerList other = (FabricChatPlayerList) obj;
			return this.parser.equals(other.parser) && this.playerList.equals(other.playerList);
		} else {
			return false;
		}
	}
}