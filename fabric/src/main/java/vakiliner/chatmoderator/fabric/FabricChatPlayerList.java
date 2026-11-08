package vakiliner.chatmoderator.fabric;

import java.util.Collection;
import java.util.UUID;
import com.google.common.collect.Collections2;
import net.minecraft.server.players.PlayerList;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.base.ChatPlayerList;
import vakiliner.chatmoderator.base.ChatServer;

public class FabricChatPlayerList extends vakiliner.chatcomponentapi.fabric.FabricChatPlayerList implements ChatPlayerList {
	private final FabricChatModerator manager;

	public FabricChatPlayerList(FabricChatModerator manager, PlayerList playerList) {
		super(FabricChatModerator.PARSER, playerList);
		this.manager = manager;
	}

	@Override
	public ChatServer getServer() {
		return this.manager.toChatServer(this.playerList.getServer());
	}

	@Override
	public Collection<? extends ChatPlayer> getPlayers() {
		return Collections2.transform(this.playerList.getPlayers(), this.manager::toChatPlayer);
	}

	@Override
	public ChatPlayer getPlayer(UUID uuid) {
		return this.manager.toChatPlayer(this.playerList.getPlayer(uuid));
	}

	@Override
	public ChatPlayer getPlayer(String name) {
		return this.manager.toChatPlayer(this.playerList.getPlayerByName(name));
	}
}
