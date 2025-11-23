package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;
import vakiliner.chatcomponentapi.paper.PaperParser;
import vakiliner.chatcomponentapi.spigot.SpigotParser;

public class ChatComponentAPIBukkitLoader {
	public static BukkitParser PARSER;

	static {
		load0();
	}

	private static synchronized BukkitParser load0() {
		if (PARSER != null) {
			return PARSER;
		}
		BukkitParser impl;
		try {
			impl = new PaperParser();
		} catch (NoClassDefFoundError a) {
			try {
				impl = new SpigotParser();
			} catch (NoClassDefFoundError b) {
				impl = new BukkitParser();
			}
		}
		return PARSER = impl;
	}

	@Deprecated
	public static BukkitParser load() {
		if (PARSER == null) {
			return load0();
		}
		return PARSER;
	}

	@Deprecated
	public static BukkitParser getBukkit() {
		return PARSER;
	}

	@Deprecated
	public static SpigotParser getSpigot() {
		return (SpigotParser) PARSER;
	}

	@Deprecated
	public static PaperParser getPaper() {
		return (PaperParser) PARSER;
	}
}