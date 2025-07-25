package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

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

	public void sendMessage(String message) {
		this.sendMessage(Util.NIL_UUID, message);
	}

	public void sendMessage(ChatComponent component) {
		this.sendMessage(Util.NIL_UUID, component);
	}

	public void sendMessage(UUID uuid, String message) {
		this.sendMessage(uuid, new ChatTextComponent(message));
	}

	public void sendMessage(UUID uuid, ChatComponent component) {
		this.commandSource.sendMessage(ForgeParser.forge(component), uuid);
	}
}