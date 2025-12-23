package vakiliner.chatmoderator.fabric;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import vakiliner.chatmoderator.base.ILoader;

public class ChatModeratorModInitializer implements ModInitializer, ILoader {
	public static final FabricChatModerator MANAGER;
	private final FabricListener listener = MANAGER.createListener();
	private ClassLoader classLoader;

	static {
		MANAGER = new FabricChatModerator();
	}

	public void onInitialize() {
		this.classLoader = this.getClass().getClassLoader();
		MANAGER.init(this);
		CommandRegistrationCallback.EVENT.register(this.listener);
		ServerLifecycleEvents.SERVER_STARTING.register(this.listener);
		ServerLifecycleEvents.SERVER_STOPPING.register(this.listener);
		ServerLifecycleEvents.SERVER_STOPPED.register(this.listener);
		FabricChatModerator.LOGGER.info("Готов. Ждёт активации сервера");
	}

	public void saveDefaultConfig() {
		if (!MANAGER.getConfigPath().toFile().exists()) {
			this.saveResource("config.json", false);
		}
	}

	public void saveResource(String resourcePath, boolean replace) {
		InputStream in = this.classLoader.getResourceAsStream(resourcePath);
		if (in == null) {
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
		}
		File dataFolder = MANAGER.getDataFolder();
		File outFile = new File(dataFolder, resourcePath);
		int lastIndex = resourcePath.lastIndexOf(47);
		File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if (outFile.exists() && !replace) {
				FabricChatModerator.LOGGER.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			} else {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];

				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				out.close();
				in.close();
			}
		} catch (IOException err) {
			FabricChatModerator.LOGGER.warn("Could not save " + outFile.getName() + " to " + outFile);
		}
	}
}