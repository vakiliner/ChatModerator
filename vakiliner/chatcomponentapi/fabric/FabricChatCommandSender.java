package vakiliner.chatcomponentapi.fabric;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

public class FabricChatCommandSender implements ChatCommandSender {
	protected final FabricParser parser;
	protected final CommandSource commandSource;

	public FabricChatCommandSender(FabricParser parser, CommandSource commandSource) {
		this.parser = parser;
		this.commandSource = commandSource;
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
		this.commandSource.sendMessage(FabricParser.fabric(component), uuid);
	}
}