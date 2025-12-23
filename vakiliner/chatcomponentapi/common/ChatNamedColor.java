package vakiliner.chatcomponentapi.common;

import java.util.Map;
import com.google.common.collect.Maps;

public class ChatNamedColor extends ChatTextColor {
	private static final Map<ChatTextFormat, ChatNamedColor> BY_FORMAT = Maps.newHashMap();
	private static final Map<String, ChatNamedColor> BY_NAME = Maps.newHashMap();
	private static final Map<Integer, ChatNamedColor> BY_VALUE = Maps.newHashMap();
	public static final ChatNamedColor BLACK = new ChatNamedColor(ChatTextFormat.BLACK, "black", 0);
	public static final ChatNamedColor DARK_BLUE = new ChatNamedColor(ChatTextFormat.DARK_BLUE, "dark_blue", 170);
	public static final ChatNamedColor DARK_GREEN = new ChatNamedColor(ChatTextFormat.DARK_GREEN, "dark_green", 43520);
	public static final ChatNamedColor DARK_AQUA = new ChatNamedColor(ChatTextFormat.DARK_AQUA, "dark_aqua", 43690);
	public static final ChatNamedColor DARK_RED = new ChatNamedColor(ChatTextFormat.DARK_RED, "dark_red", 11141120);
	public static final ChatNamedColor DARK_PURPLE = new ChatNamedColor(ChatTextFormat.DARK_PURPLE, "dark_purple", 11141290);
	public static final ChatNamedColor GOLD = new ChatNamedColor(ChatTextFormat.GOLD, "gold", 16755200);
	public static final ChatNamedColor GRAY = new ChatNamedColor(ChatTextFormat.GRAY, "gray", 11184810);
	public static final ChatNamedColor DARK_GRAY = new ChatNamedColor(ChatTextFormat.DARK_GRAY, "dark_gray", 5592405);
	public static final ChatNamedColor BLUE = new ChatNamedColor(ChatTextFormat.BLUE, "blue", 5592575);
	public static final ChatNamedColor GREEN = new ChatNamedColor(ChatTextFormat.GREEN, "green", 5635925);
	public static final ChatNamedColor AQUA = new ChatNamedColor(ChatTextFormat.AQUA, "aqua", 5636095);
	public static final ChatNamedColor RED = new ChatNamedColor(ChatTextFormat.RED, "red", 16733525);
	public static final ChatNamedColor LIGHT_PURPLE = new ChatNamedColor(ChatTextFormat.LIGHT_PURPLE, "light_purple", 16733695);
	public static final ChatNamedColor YELLOW = new ChatNamedColor(ChatTextFormat.YELLOW, "yellow", 16777045);
	public static final ChatNamedColor WHITE = new ChatNamedColor(ChatTextFormat.WHITE, "white", 16777215);
	public static final ChatNamedColor RESET = new ChatNamedColor(ChatTextFormat.RESET, "reset", -1);
	private final String name;

	private ChatNamedColor(ChatTextFormat format, String name, int color) {
		super(format, color);
		this.name = name;
		BY_FORMAT.put(this.asFormat, this);
		BY_NAME.put(this.name, this);
		BY_VALUE.put(this.value, this);
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.getName();
	}

	public static ChatNamedColor getByFormat(ChatTextFormat format) {
		return BY_FORMAT.get(format);
	}

	public static ChatNamedColor getByName(String name) {
		return BY_NAME.get(name);
	}

	public static ChatNamedColor getByValue(int value) {
		return BY_VALUE.get(value);
	}
}