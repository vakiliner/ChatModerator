package vakiliner.chatmoderator.fabric;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import vakiliner.chatmoderator.api.GsonConfig;

public class ChatModeratorModInitializer implements ModInitializer {
	public static final FabricChatModerator MANAGER;
	private final FabricListener listener = MANAGER.createListener();
	private ClassLoader classLoader;

	static {
		MANAGER = new FabricChatModerator();
	}

	public void onInitialize() {
		this.classLoader = this.getClass().getClassLoader();
		MANAGER.init(this);
		this.saveDefaultConfig();
		if (!MANAGER.getAutoModerationRulesPath().toFile().exists()) {
			this.saveResource("auto_moderation_rules.json", false);
		}
		this.reloadConfig();
		String dictionaryFile = MANAGER.config.dictionaryFile();
		if (dictionaryFile != null && dictionaryFile.equals("dictionary_ru.json")) {
			if (!MANAGER.getAutoModerationDictionaryPath().toFile().exists()) {
				this.saveResource("dictionary_ru.json", false);
			}
		}
		try {
			MANAGER.automod.reload();
		} catch (IOException err) {
			err.printStackTrace();
		}
		CommandRegistrationCallback.EVENT.register(this.listener::onCommandRegistration);
		ServerLifecycleEvents.SERVER_STARTING.register(this.listener::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPED.register(this.listener::onServerStopped);
		FabricChatModerator.LOGGER.info("Готов. Ждёт активации сервера");
	}

	public void saveDefaultConfig() {
		if (!MANAGER.getConfigPath().toFile().exists()) {
			this.saveResource("config.json", false);
		}
	}

	public void reloadConfig() {
		Path path = MANAGER.getConfigPath();
		GsonConfig config;
		if (path.toFile().exists()) {
			try {
				config = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonConfig.class);
			} catch (IOException err) {
				err.printStackTrace();
				return;
			}
		} else {
			config = null;
		}
		MANAGER.config.reload(config);
	}

	private void saveResource(String resourcePath, boolean replace) {
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