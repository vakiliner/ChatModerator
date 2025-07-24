package vakiliner.chatcomponentapi;

import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;
import vakiliner.chatcomponentapi.paper.PaperParser;
import vakiliner.chatcomponentapi.spigot.SpigotParser;

public class ChatComponentAPIBukkitLoader {
	private static BukkitParser PARSER;

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

	public static BukkitParser load() {
		if (PARSER == null) {
			return load0();
		}
		return PARSER;
	}
}