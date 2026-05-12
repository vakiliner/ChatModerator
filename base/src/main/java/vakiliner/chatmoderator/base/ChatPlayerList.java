package vakiliner.chatmoderator.base;

import java.util.Collection;
import java.util.UUID;

public interface ChatPlayerList extends vakiliner.chatcomponentapi.base.ChatPlayerList {
	ChatServer getServer();

	Collection<? extends ChatPlayer> getPlayers();

	ChatPlayer getPlayer(UUID uuid);

	ChatPlayer getPlayer(String name);
}