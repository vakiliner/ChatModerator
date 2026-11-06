package vakiliner.chatmoderator.base;

import java.util.Collection;
import java.util.UUID;

public interface ChatPlayerList extends vakiliner.chatcomponentapi.base.ChatPlayerList {
	@Override
	ChatServer getServer();

	@Override
	Collection<? extends ChatPlayer> getPlayers();

	@Override
	ChatPlayer getPlayer(UUID uuid);

	@Override
	ChatPlayer getPlayer(String name);
}
