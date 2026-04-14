package vakiliner.chatmoderator.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ChatModeratorModInitializer implements ModInitializer {
	public static final FabricChatModerator MANAGER;
	private final FabricListener listener = MANAGER.createListener();

	static {
		MANAGER = new FabricChatModerator();
	}

	public void onInitialize() {
		MANAGER.init(this);
		CommandRegistrationCallback.EVENT.register(this.listener);
		ServerLifecycleEvents.SERVER_STARTING.register(this.listener);
		ServerLifecycleEvents.SERVER_STOPPING.register(this.listener);
		ServerLifecycleEvents.SERVER_STOPPED.register(this.listener);
		FabricChatModerator.LOGGER.info("Ready. Waiting for the server to start");
	}
}