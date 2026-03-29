package vakiliner.chatcomponentapi.common;

import java.util.Map;
import com.google.common.collect.Maps;

public enum ChatTextFormat {
	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	OBFUSCATED('k', true),
	BOLD('l', true),
	STRIKETHROUGH('m', true),
	UNDERLINE('n', true),
	ITALIC('o', true),
	RESET('r');

	public static final char COLOR_CHAR = '§';
	private static final Map<Character, ChatTextFormat> BY_CHAR = Maps.newHashMap();
	private static final Map<String, ChatTextFormat> BY_NAME = Maps.newHashMap();
	private final char code;
	private final String name;
	private final boolean isFormat;
	private final String string;

	private ChatTextFormat(char code) {
		this(code, false);
	}

	private ChatTextFormat(char code, boolean isFormat) {
		this.code = code;
		this.name = this.name().toLowerCase();
		this.isFormat = isFormat;
		this.string = new String(new char[] { COLOR_CHAR, code });
	}

	public char getChar() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.string;
	}

	public boolean isFormat() {
		return this.isFormat;
	}

	public boolean isColor() {
		return !this.isFormat() && this != RESET;
	}

	public static ChatTextFormat getByChar(char code) {
		return BY_CHAR.get(code);
	}

	public static ChatTextFormat getByName(String name) {
		return BY_NAME.get(name);
	}

	static {
		for (ChatTextFormat color : values()) {
			BY_CHAR.put(color.code, color);
			BY_NAME.put(color.name, color);
		}
	}
}