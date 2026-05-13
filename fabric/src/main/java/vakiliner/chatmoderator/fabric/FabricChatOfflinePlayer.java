package vakiliner.chatmoderator.fabric;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class FabricChatOfflinePlayer extends vakiliner.chatcomponentapi.fabric.FabricChatOfflinePlayer implements ChatOfflinePlayer {
	private final FabricChatModerator manager;

	public FabricChatOfflinePlayer(FabricChatModerator manager, MinecraftServer server, GameProfile gameProfile) {
		super(FabricChatModerator.PARSER, server, gameProfile);
		this.manager = manager;
	}

	@Deprecated
	public FabricChatOfflinePlayer(FabricChatModerator manager, GameProfile gameProfile) {
		this(manager, manager.server, gameProfile);
	}

	public MutedPlayer getMute(Date filterExpired) {
		return this.manager.mutes.get(this.getUniqueId(), filterExpired);
	}

	public boolean isBypassModeration() {
		return this.server.isSingleplayerOwner(this.gameProfile);
	}

	public boolean isBypassMutes() {
		return this.isOp();
	}
}