package vakiliner.chatcomponentapi.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.gson.IGsonSerializer;

public class ChatStyle implements IGsonSerializer {
	public static final ChatStyle EMPTY = new ChatStyle(null, null, null, null, null, null, null, null, null, null);
	private final ChatTextColor color;
	private final Boolean bold;
	private final Boolean italic;
	private final Boolean underlined;
	private final Boolean strikethrough;
	private final Boolean obfuscated;
	private final ChatClickEvent clickEvent;
	private final ChatHoverEvent<?> hoverEvent;
	private final String insertion;
	private final ChatId font;

	private ChatStyle(ChatTextColor color, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ChatClickEvent clickEvent, ChatHoverEvent<?> hoverEvent, String insertion, ChatId font) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
		this.insertion = insertion;
		this.font = font;
	}

	private ChatStyle(ChatStyle style) {
		this.color = style.color;
		this.bold = style.bold;
		this.italic = style.italic;
		this.underlined = style.underlined;
		this.strikethrough = style.strikethrough;
		this.obfuscated = style.obfuscated;
		this.clickEvent = style.clickEvent;
		this.hoverEvent = style.hoverEvent;
		this.insertion = style.insertion;
		this.font = style.font;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(ChatStyle style) {
		return new Builder(style);
	}

	public Builder toBuilder() {
		return newBuilder(this);
	}

	public ChatStyle clone() {
		return new ChatStyle(this);
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public ChatTextColor getColor() {
		return this.color;
	}

	public Boolean getBold() {
		return this.bold;
	}

	public Boolean getItalic() {
		return this.italic;
	}

	public Boolean getUnderlined() {
		return this.underlined;
	}

	public Boolean getStrikethrough() {
		return this.strikethrough;
	}

	public Boolean getObfuscated() {
		return this.obfuscated;
	}

	public ChatClickEvent getClickEvent() {
		return this.clickEvent;
	}

	public ChatHoverEvent<?> getHoverEvent() {
		return this.hoverEvent;
	}

	public String getInsertion() {
		return this.insertion;
	}

	public ChatId getFont() {
		return this.font;
	}

	public ChatStyle withColor(ChatTextColor color) {
		if (Objects.equals(this.color, color)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withBold(Boolean bold) {
		if (Objects.equals(this.bold, bold)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withItalic(Boolean italic) {
		if (Objects.equals(this.italic, italic)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withUnderlined(Boolean underlined) {
		if (Objects.equals(this.underlined, underlined)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withStrikethrough(Boolean strikethrough) {
		if (Objects.equals(this.strikethrough, strikethrough)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withObfuscated(Boolean obfuscated) {
		if (Objects.equals(this.obfuscated, obfuscated)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withClickEvent(ChatClickEvent clickEvent) {
		if (Objects.equals(this.clickEvent, clickEvent)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withHoverEvent(ChatHoverEvent<?> hoverEvent) {
		if (Objects.equals(this.hoverEvent, hoverEvent)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withInsertion(String insertion) {
		if (Objects.equals(this.insertion, insertion)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public ChatStyle withFont(ChatId font) {
		if (Objects.equals(this.font, font)) return this;
		return new ChatStyle(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
	}

	public Boolean getFormat(ChatComponentFormat format) {
		switch (format) {
			case BOLD: return this.getBold();
			case ITALIC: return this.getItalic();
			case UNDERLINED: return this.getUnderlined();
			case STRIKETHROUGH: return this.getStrikethrough();
			case OBFUSCATED: return this.getObfuscated();
			default: throw new IllegalArgumentException("Invalid ChatComponentFormat");
		}
	}

	public Map<ChatComponentFormat, Boolean> getFormats() {
		Map<ChatComponentFormat, Boolean> map = new HashMap<>();
		for (ChatComponentFormat format : ChatComponentFormat.values()) {
			map.put(format, this.getFormat(format));
		}
		return map;
	}

	public ChatStyle withFormat(ChatComponentFormat format) {
		return this.withFormat(format, Boolean.TRUE);
	}

	public ChatStyle withFormat(ChatComponentFormat format, Boolean value) {
		switch (format) {
			case BOLD: return this.withBold(value);
			case ITALIC: return this.withItalic(value);
			case UNDERLINED: return this.withUnderlined(value);
			case STRIKETHROUGH: return this.withStrikethrough(value);
			case OBFUSCATED: return this.withObfuscated(value);
			default: throw new IllegalArgumentException("Invalid ChatComponentFormat");
		}
	}

	public ChatStyle withFormats(Map<ChatComponentFormat, Boolean> map) {
		return this.toBuilder().withFormats(map).build();
	}

	public ChatStyle applyTo(ChatStyle style) {
		if (this == EMPTY) {
			return style;
		} else if (style == EMPTY) {
			return this;
		} else return new ChatStyle(
			this.color != null ? this.color : style.color,
			this.bold != null ? this.bold : style.bold,
			this.italic != null ? this.italic : style.italic,
			this.underlined != null ? this.underlined : style.underlined,
			this.strikethrough != null ? this.strikethrough : style.strikethrough,
			this.obfuscated != null ? this.obfuscated : style.obfuscated,
			this.clickEvent != null ? this.clickEvent : style.clickEvent,
			this.hoverEvent != null ? this.hoverEvent : style.hoverEvent,
			this.insertion != null ? this.insertion : style.insertion,
			this.font != null ? this.font : style.font
		);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof ChatStyle) {
			ChatStyle other = (ChatStyle) obj;
			return (Objects.equals(this.color,			other.color)
				 && Objects.equals(this.bold,			other.bold)
				 && Objects.equals(this.italic,			other.italic)
				 && Objects.equals(this.underlined,		other.underlined)
				 && Objects.equals(this.strikethrough,	other.strikethrough)
				 && Objects.equals(this.obfuscated,		other.obfuscated)
				 && Objects.equals(this.insertion,		other.insertion)
				 && Objects.equals(this.clickEvent,		other.clickEvent)
				 && Objects.equals(this.hoverEvent,		other.hoverEvent)
				 && Objects.equals(this.font,			other.font)
			);
		} else {
			return false;
		}
	}

	public JsonElement serialize() {
		return serialize(this);
	}

	public static JsonElement serialize(ChatStyle style) {
		return serialize(style, new JsonObject());
	}

	public static JsonObject serialize(ChatStyle style, JsonObject object) {
		Objects.requireNonNull(object);
		ChatTextColor color = style.color;
		Boolean bold = style.bold;
		Boolean italic = style.italic;
		Boolean underlined = style.underlined;
		Boolean strikethrough = style.strikethrough;
		Boolean obfuscated = style.obfuscated;
		ChatClickEvent clickEvent = style.clickEvent;
		ChatHoverEvent<?> hoverEvent = style.hoverEvent;
		String insertion = style.insertion;
		ChatId font = style.font;
		if (color != null) object.addProperty("color", color.toString());
		if (bold != null) object.addProperty("bold", bold);
		if (italic != null) object.addProperty("italic", italic);
		if (underlined != null) object.addProperty("underlined", underlined);
		if (strikethrough != null) object.addProperty("strikethrough", strikethrough);
		if (obfuscated != null) object.addProperty("obfuscated", obfuscated);
		if (clickEvent != null) object.add("clickEvent", ChatClickEvent.serialize(clickEvent));
		if (hoverEvent != null) object.add("hoverEvent", ChatHoverEvent.serialize(hoverEvent));
		if (insertion != null) object.addProperty("insertion", insertion);
		if (font != null) object.addProperty("font", font.toString());
		return object;
	}

	public static ChatStyle deserialize(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		JsonElement color = object.get("color");
		JsonElement bold = object.get("bold");
		JsonElement italic = object.get("italic");
		JsonElement underlined = object.get("underlined");
		JsonElement strikethrough = object.get("strikethrough");
		JsonElement obfuscated = object.get("obfuscated");
		JsonElement clickEvent = object.get("clickEvent");
		JsonElement hoverEvent = object.get("hoverEvent");
		JsonElement insertion = object.get("insertion");
		JsonElement font = object.get("font");
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		if (color != null) builder.withColor(ChatTextColor.of(color.getAsString()));
		if (bold != null) builder.withBold(bold.getAsBoolean());
		if (italic != null) builder.withItalic(italic.getAsBoolean());
		if (underlined != null) builder.withUnderlined(underlined.getAsBoolean());
		if (strikethrough != null) builder.withStrikethrough(strikethrough.getAsBoolean());
		if (obfuscated != null) builder.withObfuscated(obfuscated.getAsBoolean());
		if (clickEvent != null) builder.withClickEvent(ChatClickEvent.deserialize(clickEvent));
		if (hoverEvent != null) builder.withHoverEvent(ChatHoverEvent.deserialize(hoverEvent));
		if (insertion != null) builder.withInsertion(insertion.getAsString());
		if (font != null) builder.withFont(ChatId.parse(font.getAsString()));
		return builder.build();
	}

	public static class Builder {
		private ChatTextColor color;
		private Boolean bold;
		private Boolean italic;
		private Boolean underlined;
		private Boolean strikethrough;
		private Boolean obfuscated;
		private String insertion;
		private ChatClickEvent clickEvent;
		private ChatHoverEvent<?> hoverEvent;
		private ChatId font;

		private Builder() {
		}

		private Builder(ChatStyle style) {
			this.color = style.color;
			this.bold = style.bold;
			this.italic = style.italic;
			this.underlined = style.underlined;
			this.strikethrough = style.strikethrough;
			this.obfuscated = style.obfuscated;
			this.insertion = style.insertion;
			this.clickEvent = style.clickEvent;
			this.hoverEvent = style.hoverEvent;
			this.font = style.font;
		}

		public Builder withColor(ChatTextColor color) {
			this.color = color;
			return this;
		}

		public Builder withBold(Boolean bold) {
			this.bold = bold;
			return this;
		}

		public Builder withItalic(Boolean italic) {
			this.italic = italic;
			return this;
		}

		public Builder withUnderlined(Boolean underlined) {
			this.underlined = underlined;
			return this;
		}

		public Builder withStrikethrough(Boolean strikethrough) {
			this.strikethrough = strikethrough;
			return this;
		}

		public Builder withObfuscated(Boolean obfuscated) {
			this.obfuscated = obfuscated;
			return this;
		}

		public Builder withClickEvent(ChatClickEvent clickEvent) {
			this.clickEvent = clickEvent;
			return this;
		}

		public Builder withHoverEvent(ChatHoverEvent<?> hoverEvent) {
			this.hoverEvent = hoverEvent;
			return this;
		}

		public Builder withInsertion(String insertion) {
			this.insertion = insertion;
			return this;
		}

		public Builder withFont(ChatId font) {
			this.font = font;
			return this;
		}

		public Builder withFormat(ChatComponentFormat format, Boolean value) {
			switch (format) {
				case BOLD: return this.withBold(value);
				case ITALIC: return this.withItalic(value);
				case UNDERLINED: return this.withUnderlined(value);
				case STRIKETHROUGH: return this.withStrikethrough(value);
				case OBFUSCATED: return this.withObfuscated(value);
				default: throw new IllegalArgumentException("Invalid ChatComponentFormat");
			}
		}

		public Builder withFormats(Map<ChatComponentFormat, Boolean> map) {
			for (Map.Entry<ChatComponentFormat, Boolean> entry : map.entrySet()) {
				this.withFormat(entry.getKey(), entry.getValue());
			}
			return this;
		}

		public ChatStyle build() {
			return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
		}
	}
}