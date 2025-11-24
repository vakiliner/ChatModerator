package vakiliner.chatmoderator.forge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vakiliner.chatmoderator.base.ILoader;

@Mod(ForgeChatModerator.ID)
public class ChatModeratorModInitializer implements ILoader {
	public static final ForgeChatModerator MANAGER;
	protected final ModContainer modContainer;
	private final ForgeListener listener = MANAGER.createListener();
	private ClassLoader classLoader;

	static {
		MANAGER = new ForgeChatModerator();
	}

	public ChatModeratorModInitializer() {
		this(ModLoadingContext.get());
	}

	public ChatModeratorModInitializer(ModLoadingContext context) {
		this.modContainer = context.getActiveContainer();
		this.classLoader = this.getClass().getClassLoader();
		MANAGER.init(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(MANAGER);
		MinecraftForge.EVENT_BUS.register(this.listener);
		ForgeChatModerator.LOGGER.info("Готов. Ждёт активации сервера");
	}

	public void saveDefaultConfig() {
		if (!MANAGER.getConfigPath().toFile().exists()) {
			this.saveResource("config.toml", false);
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
				ForgeChatModerator.LOGGER.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
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
			ForgeChatModerator.LOGGER.warn("Could not save " + outFile.getName() + " to " + outFile);
		}
	}
}