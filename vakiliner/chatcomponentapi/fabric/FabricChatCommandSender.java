package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class FabricChatCommandSender implements ChatCommandSender {
	protected final FabricParser parser;
	protected final CommandSource commandSource;

	public FabricChatCommandSender(FabricParser parser, CommandSource commandSource) {
		this.parser = Objects.requireNonNull(parser);
		this.commandSource = Objects.requireNonNull(commandSource);
	}

	public boolean isConsole() {
		return commandSource instanceof MinecraftServer;
	}

	public String getName() {
		return "CONSOLE";
	}

	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.commandSource, component, type, uuid);
	}
}