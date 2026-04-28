package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.forge.ForgeParser;

public class ChatComponentAPIForgeLoader {
	public static final ForgeParser PARSER = new ForgeParser();

	@Deprecated
	public static ForgeParser load() {
		return PARSER;
	}
}