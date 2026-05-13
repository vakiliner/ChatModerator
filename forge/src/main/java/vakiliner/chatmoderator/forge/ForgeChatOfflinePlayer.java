package vakiliner.chatmoderator.forge;

import java.util.Date;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatmoderator.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.core.MutedPlayer;

public class ForgeChatOfflinePlayer extends vakiliner.chatcomponentapi.forge.ForgeChatOfflinePlayer implements ChatOfflinePlayer {
	private final ForgeChatModerator manager;

	public ForgeChatOfflinePlayer(ForgeChatModerator manager, MinecraftServer server, GameProfile gameProfile) {
		super(ForgeChatModerator.PARSER, server, gameProfile);
		this.manager = manager;
	}

	@Deprecated
	public ForgeChatOfflinePlayer(ForgeChatModerator manager, GameProfile gameProfile) {
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