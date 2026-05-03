package vakiliner.chatcomponentapi.base;

import java.util.UUID;
import com.mojang.authlib.GameProfile;

public interface ChatOfflinePlayer {
	GameProfile getGameProfile();

	default String getName() {
		return this.getGameProfile().getName();
	}

	default UUID getUniqueId() {
		return this.getGameProfile().getId();
	}

	boolean isOp();

	ChatTeam getTeam();
}