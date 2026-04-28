package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatCommandSender implements ChatCommandSender {
	protected final ForgeParser parser;
	protected final ICommandSource commandSource;

	public ForgeChatCommandSender(ForgeParser parser, ICommandSource commandSource) {
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