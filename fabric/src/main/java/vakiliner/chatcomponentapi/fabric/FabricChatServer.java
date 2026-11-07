package vakiliner.chatcomponentapi.fabric;

import static java.util.Objects.requireNonNull;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class FabricChatServer implements ChatServer {
	protected final FabricParser parser;
	protected final MinecraftServer server;

	public FabricChatServer(FabricParser parser, MinecraftServer server) {
		this.parser = requireNonNull(parser);
		this.server = requireNonNull(server);
	}

	public MinecraftServer getImpl() {
		return this.server;
	}

	@Override
	public ChatPlayerList getPlayerList() {
		return this.parser.toChatPlayerList(this.server.getPlayerList());
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.server, component, type, uuid);
	}

	@Override
	public void execute(Runnable command) {
		this.server.execute(command);
	}

	@Override
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
