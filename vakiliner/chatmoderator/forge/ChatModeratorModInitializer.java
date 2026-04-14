package vakiliner.chatmoderator.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ForgeChatModerator.ID)
public class ChatModeratorModInitializer {
	public static final ForgeChatModerator MANAGER;
	private final ForgeListener listener = MANAGER.createListener();

	static {
		MANAGER = new ForgeChatModerator();
	}

	public ChatModeratorModInitializer() {
		MANAGER.init(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(MANAGER);
		MinecraftForge.EVENT_BUS.register(this.listener);
		ForgeChatModerator.LOGGER.info("Ready. Waiting for the server to start");
	}
}