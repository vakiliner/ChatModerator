package vakiliner.chatcomponentapi.common;

import java.util.Map;
import com.google.common.collect.Maps;

public class ChatNamedColor extends ChatTextColor {
	private static final Map<ChatTextFormat, ChatNamedColor> BY_FORMAT = Maps.newHashMap();
	private static final Map<Integer, ChatNamedColor> BY_VALUE = Maps.newHashMap();
	public static final ChatNamedColor BLACK = new ChatNamedColor(ChatTextFormat.BLACK, 0);
	public static final ChatNamedColor DARK_BLUE = new ChatNamedColor(ChatTextFormat.DARK_BLUE, 170);
	public static final ChatNamedColor DARK_GREEN = new ChatNamedColor(ChatTextFormat.DARK_GREEN, 43520);
	public static final ChatNamedColor DARK_AQUA = new ChatNamedColor(ChatTextFormat.DARK_AQUA, 43690);
	public static final ChatNamedColor DARK_RED = new ChatNamedColor(ChatTextFormat.DARK_RED, 11141120);
	public static final ChatNamedColor DARK_PURPLE = new ChatNamedColor(ChatTextFormat.DARK_PURPLE, 11141290);
	public static final ChatNamedColor GOLD = new ChatNamedColor(ChatTextFormat.GOLD, 16755200);
	public static final ChatNamedColor GRAY = new ChatNamedColor(ChatTextFormat.GRAY, 11184810);
	public static final ChatNamedColor DARK_GRAY = new ChatNamedColor(ChatTextFormat.DARK_GRAY, 5592405);
	public static final ChatNamedColor BLUE = new ChatNamedColor(ChatTextFormat.BLUE, 5592575);
	public static final ChatNamedColor GREEN = new ChatNamedColor(ChatTextFormat.GREEN, 5635925);
	public static final ChatNamedColor AQUA = new ChatNamedColor(ChatTextFormat.AQUA, 5636095);
	public static final ChatNamedColor RED = new ChatNamedColor(ChatTextFormat.RED, 16733525);
	public static final ChatNamedColor LIGHT_PURPLE = new ChatNamedColor(ChatTextFormat.LIGHT_PURPLE, 16733695);
	public static final ChatNamedColor YELLOW = new ChatNamedColor(ChatTextFormat.YELLOW, 16777045);
	public static final ChatNamedColor WHITE = new ChatNamedColor(ChatTextFormat.WHITE, 16777215);
	public static final ChatNamedColor RESET = new ChatNamedColor(ChatTextFormat.RESET, -1);

	private ChatNamedColor(ChatTextFormat format, int color) {
		super(format, color);
		if (this.asFormat.isFormat()) throw new IllegalArgumentException("ChatTextFormat cannot be a format");
		BY_FORMAT.put(this.asFormat, this);
		BY_VALUE.put(this.value, this);
	}

	public String getName() {
		return this.asFormat.getName();
	}

	public static ChatNamedColor getByFormat(ChatTextFormat format) {
		return BY_FORMAT.get(format);
	}

	public static ChatNamedColor getByValue(int value) {
		return BY_VALUE.get(value);
	}

	public static ChatNamedColor getByName(String name) {
		return getByFormat(ChatTextFormat.getByName(name));
	}
}