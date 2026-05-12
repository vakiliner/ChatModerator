package vakiliner.chatmoderator.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import vakiliner.chatcomponentapi.fabric.IFabricChatPlugin;

public class ChatModeratorModInitializer implements ModInitializer, IFabricChatPlugin {
	public static final FabricChatModerator MANAGER;
	private final FabricListener listener = MANAGER.createListener();
	protected final ModContainer modContainer = FabricLoader.getInstance().getModContainer(FabricChatModerator.ID).get();

	static {
		MANAGER = new FabricChatModerator();
	}

	public void onInitialize() {
		MANAGER.init(this);
		try {
			CommandRegistrationCallback.EVENT.register(this.listener::register);
		} catch (NoClassDefFoundError err) {
			String message = err.getMessage();
			if (message == null || !message.equals("net/fabricmc/fabric/api/command/v1/CommandRegistrationCallback")) {
				throw err;
			}
			FabricChatModerator.LOGGER.warn("Mod fabric-command-api-v1 not found, commands not registered");
		}
		ServerLifecycleEvents.SERVER_STARTED.register(this.listener);
		ServerLifecycleEvents.SERVER_STOPPING.register(this.listener);
		ServerLifecycleEvents.SERVER_STOPPED.register(this.listener);
		FabricChatModerator.LOGGER.info("Ready. Waiting for the server to start");
	}
}