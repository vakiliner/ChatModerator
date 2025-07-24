package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.forge.ForgeParser;

public class ChatComponentAPIForgeLoader {
	private static ForgeParser PARSER;

	private static synchronized ForgeParser load0() {
		if (PARSER != null) {
			return PARSER;
		}
		ForgeParser impl;
		impl = new ForgeParser();
		return PARSER = impl;
	}

	public static ForgeParser load() {
		if (PARSER == null) {
			return load0();
		}
		return PARSER;
	}
}