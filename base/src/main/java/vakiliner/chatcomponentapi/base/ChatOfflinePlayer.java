package vakiliner.chatcomponentapi.base;

import java.util.UUID;

public interface ChatOfflinePlayer {
	String getName();

	UUID getUniqueId();

	boolean isOp();

	ChatTeam getTeam();
}