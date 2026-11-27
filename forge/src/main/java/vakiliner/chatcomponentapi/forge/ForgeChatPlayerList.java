package vakiliner.chatcomponentapi.forge;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;
import com.google.common.collect.Collections2;
import net.minecraft.server.management.PlayerList;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatPlayerList implements ChatPlayerList {
	protected final ForgeParser parser;
	protected final PlayerList playerList;

	public ForgeChatPlayerList(ForgeParser parser, PlayerList playerList) {
		this.parser = requireNonNull(parser);
		this.playerList = requireNonNull(playerList);
	}

	public PlayerList getImpl() {
		return this.playerList;
	}

	@Override
	public ChatServer getServer() {
		return this.parser.toChatServer(this.playerList.getServer());
	}

	@Override
	public Collection<? extends ChatPlayer> getPlayers() {
		return Collections2.transform(this.playerList.getPlayers(), this.parser::toChatPlayer);
	}

	@Override
	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.playerList.getPlayer(uuid));
	}

	@Override
	public ChatPlayer getPlayer(String name) {
		return this.parser.toChatPlayer(this.playerList.getPlayerByName(name));
	}

	@Override
	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid, Predicate<? super ChatPlayer> predicate) {
		this.parser.broadcastMessage(this.playerList, component, type, uuid, predicate);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatPlayerList other = (ForgeChatPlayerList) obj;
			return this.parser.equals(other.parser) && this.playerList.equals(other.playerList);
		} else {
			return false;
		}
	}
}
