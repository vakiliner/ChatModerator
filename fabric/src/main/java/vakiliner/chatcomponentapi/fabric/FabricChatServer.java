package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class FabricChatServer implements ChatServer {
	protected final FabricParser parser;
	protected final MinecraftServer server;

	public FabricChatServer(FabricParser parser, MinecraftServer server) {
		this.parser = Objects.requireNonNull(parser);
		this.server = Objects.requireNonNull(server);
	}

	public MinecraftServer getImpl() {
		return this.server;
	}

	public ChatPlayerList getPlayerList() {
		return this.parser.toChatPlayerList(this.server.getPlayerList());
	}

	public String getName() {
		return "CONSOLE";
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.server, component, type, uuid);
	}

	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server, plugin, runnable);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatServer other = (FabricChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}