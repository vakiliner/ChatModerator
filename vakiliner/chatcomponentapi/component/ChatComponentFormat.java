package vakiliner.chatcomponentapi.component;

import java.util.Map;
import com.google.common.collect.Maps;
import vakiliner.chatcomponentapi.common.ChatTextFormat;

public enum ChatComponentFormat {
	BOLD(ChatTextFormat.BOLD, "bold"),
	ITALIC(ChatTextFormat.ITALIC, "italic"),
	UNDERLINED(ChatTextFormat.UNDERLINE, "underlined"),
	STRIKETHROUGH(ChatTextFormat.STRIKETHROUGH, "strikethrough"),
	OBFUSCATED(ChatTextFormat.MAGIC, "obfuscated");

	private static final Map<ChatTextFormat, ChatComponentFormat> BY_FORMAT = Maps.newHashMap();
	private static final Map<String, ChatComponentFormat> BY_NAME = Maps.newHashMap();
	private final ChatTextFormat text;
	private final String name;

	private ChatComponentFormat(ChatTextFormat text, String name) {
		this.text = text;
		this.name = name;
	}

	public ChatTextFormat asTextFormat() {
		return this.text;
	}

	public String getName() {
		return this.name;
	}

	public static ChatComponentFormat getByFormat(ChatTextFormat format) {
		return BY_FORMAT.get(format);
	}

	public static ChatComponentFormat getByName(String name) {
		return BY_NAME.get(name);
	}

	static {
		for (ChatComponentFormat format : values()) {
			BY_FORMAT.put(format.text, format);
			BY_NAME.put(format.name, format);
		}
	}
}