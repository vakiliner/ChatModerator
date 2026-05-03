package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.fabric.FabricParser;
import vakiliner.chatcomponentapi.fabric.IFabricChatPlugin;

public class ChatComponentAPIFabricLoader implements IFabricChatPlugin {
	public static final FabricParser PARSER = new FabricParser();

	@Deprecated
	public static FabricParser load() {
		return PARSER;
	}
}