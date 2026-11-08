package vakiliner.chatcomponentapi.fabric;

import static java.util.Objects.requireNonNull;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;

public class FabricChatOfflinePlayer implements ChatOfflinePlayer {
	protected final FabricParser parser;
	protected final MinecraftServer server;
	protected final GameProfile gameProfile;

	public FabricChatOfflinePlayer(FabricParser parser, MinecraftServer server, GameProfile gameProfile) {
		this.parser = requireNonNull(parser);
		this.server = requireNonNull(server);
		this.gameProfile = requireNonNull(gameProfile);
	}

	@Override
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	@Override
	public boolean isOp() {
		return this.server.getPlayerList().isOp(this.gameProfile);
	}

	@Override
	public ChatTeam getTeam() {
		return this.parser.toChatTeam(this.server.getScoreboard().getPlayerTeam(this.getName()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatOfflinePlayer other = (FabricChatOfflinePlayer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server) && this.gameProfile.equals(other.gameProfile);
		} else {
			return false;
		}
	}
}
