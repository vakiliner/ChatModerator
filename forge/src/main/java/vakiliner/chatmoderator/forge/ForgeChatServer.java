package vakiliner.chatmoderator.forge;

import net.minecraft.server.MinecraftServer;
import vakiliner.chatmoderator.base.ChatPlayerList;
import vakiliner.chatmoderator.base.ChatServer;

public class ForgeChatServer extends vakiliner.chatcomponentapi.forge.ForgeChatServer implements ChatServer {
	private final ForgeChatModerator manager;

	public ForgeChatServer(ForgeChatModerator manager, MinecraftServer server) {
		super(ForgeChatModerator.PARSER, server);
		this.manager = manager;
	}

	public ChatPlayerList getPlayerList() {
		return this.manager.toChatPlayerList(this.server.getPlayerList());
	}
}