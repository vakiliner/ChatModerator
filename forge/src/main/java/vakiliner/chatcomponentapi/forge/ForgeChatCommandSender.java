package vakiliner.chatcomponentapi.forge;

import static java.util.Objects.requireNonNull;
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
		this.parser = requireNonNull(parser);
		this.commandSource = requireNonNull(commandSource);
	}

	@Override
	public boolean isConsole() {
		return commandSource instanceof MinecraftServer;
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.commandSource, component, type, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatCommandSender other = (ForgeChatCommandSender) obj;
			return this.parser.equals(other.parser) && this.commandSource.equals(other.commandSource);
		} else {
			return false;
		}
	}
}
