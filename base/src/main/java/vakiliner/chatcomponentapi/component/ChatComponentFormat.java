package vakiliner.chatcomponentapi.component;

import java.util.Map;
import com.google.common.collect.Maps;
import vakiliner.chatcomponentapi.common.ChatTextFormat;

public enum ChatComponentFormat {
	BOLD(ChatTextFormat.BOLD),
	ITALIC(ChatTextFormat.ITALIC),
	UNDERLINED(ChatTextFormat.UNDERLINE),
	STRIKETHROUGH(ChatTextFormat.STRIKETHROUGH),
	OBFUSCATED(ChatTextFormat.OBFUSCATED);

	private static final Map<ChatTextFormat, ChatComponentFormat> BY_FORMAT = Maps.newHashMap();
	private static final Map<String, ChatComponentFormat> BY_NAME = Maps.newHashMap();
	private final ChatTextFormat textFormat;
	private final String name;

	private ChatComponentFormat(ChatTextFormat textFormat) {
		this.textFormat = textFormat;
		this.name = this.name().toLowerCase();
	}

	public ChatTextFormat asTextFormat() {
		return this.textFormat;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.textFormat.toString();
	}

	public static ChatComponentFormat getByFormat(ChatTextFormat format) {
		return BY_FORMAT.get(format);
	}

	public static ChatComponentFormat getByName(String name) {
		return BY_NAME.get(name);
	}

	static {
		for (ChatComponentFormat format : values()) {
			BY_FORMAT.put(format.textFormat, format);
			BY_NAME.put(format.name, format);
		}
	}
}