package vakiliner.chatcomponentapi.common;

public class ChatTextColor {
	protected final int value;
	protected final ChatTextFormat asFormat;

	private ChatTextColor(int value, ChatTextFormat asFormat) {
		this.value = value & 0xFFFFFF;
		this.asFormat = asFormat;
	}

	protected ChatTextColor(ChatTextFormat format, int value) {
		this.asFormat = format;
		this.value = value;
	}

	public static ChatTextColor color(int color, ChatTextFormat asFormat) {
		int truncatedValue = color & 0xFFFFFF;
		ChatNamedColor named = ChatNamedColor.getByValue(truncatedValue);
		return named != null ? named : new ChatTextColor(color, asFormat);
	}

	public static ChatTextColor color(int red, int green, int blue, ChatTextFormat asFormat) {
		return color((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF), asFormat);
	}

	public static ChatTextColor of(String string) {
		if (string.startsWith("#") && string.length() == 7) {
			return color(Integer.parseInt(string.substring(1), 16), null);
		} else {
			return ChatNamedColor.getByName(string);
		}
	}

	public int value() {
		return this.value;
	}

	public int red() {
		return this.value >> 16 & 0xFF;
	}

	public int green() {
		return this.value >> 8 & 0xFF;
	}

	public int blue() {
		return this.value & 0xFF;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatTextColor)) {
			return false;
		} else {
			ChatTextColor other = (ChatTextColor) obj;
			return this.value == other.value;
		}
	}

	public ChatTextFormat asFormat() {
		return this.asFormat;
	}

	public String toString() {
		return String.format("#%06X", this.value);
	}
}