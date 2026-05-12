package vakiliner.chatmoderator.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vakiliner.chatcomponentapi.forge.IForgeChatPlugin;

@Mod(ForgeChatModerator.ID)
public class ChatModeratorModInitializer implements IForgeChatPlugin {
	public static final ForgeChatModerator MANAGER;
	private final ForgeListener listener = MANAGER.createListener();
	protected final ModContainer modContainer;

	static {
		MANAGER = new ForgeChatModerator();
	}

	public ChatModeratorModInitializer() {
		this(ModLoadingContext.get());
	}

	public ChatModeratorModInitializer(ModLoadingContext context) {
		this.modContainer = context.getActiveContainer();
		MANAGER.init(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(MANAGER);
		MinecraftForge.EVENT_BUS.register(this.listener);
		ForgeChatModerator.LOGGER.info("Ready. Waiting for the server to start");
	}
}