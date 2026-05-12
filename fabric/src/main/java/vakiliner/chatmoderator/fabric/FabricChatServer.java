package vakiliner.chatmoderator.fabric;

import net.minecraft.server.MinecraftServer;
import vakiliner.chatmoderator.base.ChatPlayerList;
import vakiliner.chatmoderator.base.ChatServer;

public class FabricChatServer extends vakiliner.chatcomponentapi.fabric.FabricChatServer implements ChatServer {
	private final FabricChatModerator manager;

	public FabricChatServer(FabricChatModerator manager, MinecraftServer server) {
		super(FabricChatModerator.PARSER, server);
		this.manager = manager;
	}

	public ChatPlayerList getPlayerList() {
		return this.manager.toChatPlayerList(this.server.getPlayerList());
	}
}