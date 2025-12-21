package vakiliner.chatmoderator.fabric;

import java.io.IOException;
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
		try {
			this.manager.mutes.setup(this.manager.getMutesPath().toFile());
		} catch (IOException err) {
			throw new RuntimeException(err);
		}
	}

	public void onServerStopped(MinecraftServer server) {
		this.manager.server = null;
		try {
			this.manager.mutes.stop();
		} catch (IOException err) {
			throw new RuntimeException(err);
		}
	}

	public void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		dispatcher.register(MuteCommand.register(this.manager, dispatcher));
		dispatcher.register(UnmuteCommand.register(this.manager, dispatcher));
		dispatcher.register(MuteListCommand.register(this.manager, dispatcher));
	}
}