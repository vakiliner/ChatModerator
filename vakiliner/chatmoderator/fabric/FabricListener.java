package vakiliner.chatmoderator.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatmoderator.fabric.command.*;

class FabricListener {
	private final FabricChatModerator manager;

	protected FabricListener(FabricChatModerator manager) {
		this.manager = manager;
	}

	public void onServerStarting(MinecraftServer server) {
		this.manager.server = server;
	}

	public void onServerStopped(MinecraftServer server) {
		if (this.manager.server == server) {
			this.manager.server = null;
		}
	}

	public void onCommandRegistration(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		dispatcher.register(MuteCommand.register(this.manager, dispatcher));
		dispatcher.register(UnmuteCommand.register(this.manager, dispatcher));
		dispatcher.register(MuteListCommand.register(this.manager, dispatcher));
	}
}