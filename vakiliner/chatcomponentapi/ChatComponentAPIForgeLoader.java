package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.forge.ForgeParser;

public class ChatComponentAPIForgeLoader {
	public static ForgeParser PARSER;

	static {
		load0();
	}

	private static synchronized ForgeParser load0() {
		if (PARSER != null) {
			return PARSER;
		}
		ForgeParser impl;
		impl = new ForgeParser();
		return PARSER = impl;
	}

	@Deprecated
	public static ForgeParser load() {
		if (PARSER == null) {
			return load0();
		}
		return PARSER;
	}
}