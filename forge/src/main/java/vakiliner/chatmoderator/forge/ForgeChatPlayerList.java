package vakiliner.chatmoderator.forge;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.server.management.PlayerList;
import vakiliner.chatcomponentapi.util.ParseCollection;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatPlayerList;
import vakiliner.chatmoderator.base.ChatServer;

public class ForgeChatPlayerList extends vakiliner.chatcomponentapi.forge.ForgeChatPlayerList implements ChatPlayerList {
	private final ForgeChatModerator manager;

	public ForgeChatPlayerList(ForgeChatModerator manager, PlayerList playerList) {
		super(ForgeChatModerator.PARSER, playerList);
		this.manager = manager;
	}

	public ChatServer getServer() {
		return this.manager.toChatServer(this.playerList.getServer());
	}

	public Collection<? extends ChatPlayer> getPlayers() {
		return new ParseCollection<>(this.playerList.getPlayers(), this.manager::toChatPlayer);
	}

	public ChatPlayer getPlayer(UUID uuid) {
		return this.manager.toChatPlayer(this.playerList.getPlayer(uuid));
	}

	public ChatPlayer getPlayer(String name) {
		return this.manager.toChatPlayer(this.playerList.getPlayerByName(name));
	}
}