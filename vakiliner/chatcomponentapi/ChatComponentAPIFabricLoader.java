package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader {
	public static FabricParser PARSER;

	static {
		load0();
	}

	private static synchronized FabricParser load0() {
		if (PARSER != null) {
			return PARSER;
		}
		FabricParser impl;
		impl = new FabricParser();
		return PARSER = impl;
	}

	@Deprecated
	public static FabricParser load() {
		if (PARSER == null) {
			return load0();
		}
		return PARSER;
	}
}