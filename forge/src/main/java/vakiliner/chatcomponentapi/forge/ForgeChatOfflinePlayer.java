package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;

public class ForgeChatOfflinePlayer implements ChatOfflinePlayer {
	protected final ForgeParser parser;
	protected final MinecraftServer server;
	protected final GameProfile gameProfile;

	public ForgeChatOfflinePlayer(ForgeParser parser, MinecraftServer server, GameProfile gameProfile) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
		this.gameProfile = Objects.requireNonNull(gameProfile);
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public boolean isOp() {
		return this.server.getPlayerList().isOp(this.gameProfile);
	}

	public ChatTeam getTeam() {
		return this.parser.toChatTeam(this.server.getScoreboard().getPlayerTeam(this.getName()));
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatOfflinePlayer other = (ForgeChatOfflinePlayer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server) && this.gameProfile.equals(other.gameProfile);
		} else {
			return false;
		}
	}
}