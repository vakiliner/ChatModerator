package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
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

	public String getName() {
		return this.gameProfile.getName();
	}

	public UUID getUniqueId() {
		return this.gameProfile.getId();
	}

	public boolean isOp() {
		return this.server.getPlayerList().isOp(this.gameProfile);
	}

	public ChatTeam getTeam() {
		return this.parser.toChatTeam(this.server.getScoreboard().getPlayerTeam(this.getName()));
	}
}