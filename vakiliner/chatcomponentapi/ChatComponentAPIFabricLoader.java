package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader {
	public static final FabricParser PARSER = new FabricParser();

	@Deprecated
	public static FabricParser load() {
		return PARSER;
	}
}