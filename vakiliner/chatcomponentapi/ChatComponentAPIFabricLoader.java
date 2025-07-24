package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader {
	private static FabricParser PARSER;

	private static synchronized FabricParser load0() {
		if (PARSER != null) {
			return PARSER;
		}
		FabricParser impl;
		impl = new FabricParser();
		return PARSER = impl;
	}

	public static FabricParser load() {
		if (PARSER == null) {
			return load0();
		}
		return PARSER;
	}
}