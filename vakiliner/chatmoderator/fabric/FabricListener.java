package vakiliner.chatmoderator.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import vakiliner.chatmoderator.fabric.command.*;

class FabricListener implements CommandRegistrationCallback, ServerStarting, ServerStopped {
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

	public void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		dispatcher.register(MuteCommand.register(this.manager, dispatcher));
		dispatcher.register(UnmuteCommand.register(this.manager, dispatcher));
		dispatcher.register(MuteListCommand.register(this.manager, dispatcher));
	}
}