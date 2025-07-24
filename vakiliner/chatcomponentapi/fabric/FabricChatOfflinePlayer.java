package vakiliner.chatcomponentapi.fabric;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;

public class FabricChatOfflinePlayer implements ChatOfflinePlayer {
	protected final FabricParser parser;
	protected final MinecraftServer server;
	protected final GameProfile gameProfile;

	public FabricChatOfflinePlayer(FabricParser parser, MinecraftServer server, GameProfile gameProfile) {
		this.parser = parser;
		this.server = server;
		this.gameProfile = gameProfile;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public String getName() {
		return this.gameProfile.getName();
	}

	public UUID getUniqueId() {
		return this.gameProfile.getId();
	}

	public boolean isOp() {
		return this.server.getPlayerList().isOp(this.gameProfile);
	}

	public boolean isOnline() {
		return this.server.getPlayerList().getPlayer(this.getUniqueId()) != null;
	}

	public ChatTeam getTeam() {
		return this.parser.toChatTeam(this.server.getScoreboard().getPlayerTeam(this.getName()));
	}
}