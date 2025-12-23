package vakiliner.chatcomponentapi.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;

public abstract class ChatComponent {
	private ChatComponent parent;
	protected ChatTextColor color;
	protected Boolean bold;
	protected Boolean italic;
	protected Boolean underlined;
	protected Boolean strikethrough;
	protected Boolean obfuscated;
	protected String insertion;
	protected ChatClickEvent clickEvent;
	protected ChatHoverEvent<?> hoverEvent;
	protected List<ChatComponent> extra;

	public ChatComponent() {
	}

	public ChatComponent(ChatTextColor color) {
		this.color = color;
	}

	protected ChatComponent(ChatComponent component) {
		this.color = component.color;
		this.bold = component.bold;
		this.italic = component.italic;
		this.underlined = component.underlined;
		this.strikethrough = component.strikethrough;
		this.obfuscated = component.obfuscated;
		this.insertion = component.insertion;
		this.clickEvent = component.clickEvent;
		this.hoverEvent = component.hoverEvent;
		List<ChatComponent> extra = component.extra;
		if (extra != null) for (ChatComponent chatComponent : extra) {
			this.append(chatComponent.clone());
		}
	}

	public abstract ChatComponent clone();

	public String toLegacyText() {
		return this.toLegacyText(ChatNamedColor.RESET, Collections.unmodifiableSet(new HashSet<>()));
	}

	protected String toLegacyText(final ChatTextColor parentColor, final Set<ChatComponentFormat> parentFormats) {
		StringBuilder text = new StringBuilder();
		Set<ChatComponentFormat> formats = new HashSet<>(parentFormats);
		ChatTextColor color = this.color;
		if (color == null) color = parentColor;
		boolean reset = !color.equals(parentColor);
		for (Map.Entry<ChatComponentFormat, Boolean> entry : this.getFormatsRaw().entrySet()) {
			Boolean isSet = entry.getValue();
			if (isSet != null) {
				if (isSet) {
					formats.add(entry.getKey());
				} else if (formats.remove(entry.getKey())) {
					reset = true;
				}
			}
		}
		formats = Collections.unmodifiableSet(formats);
		if (reset) {
			ChatTextFormat textFormat = color.asFormat();
			if (textFormat == null) textFormat = ChatTextFormat.RESET;
			text.append(textFormat);
			for (ChatComponentFormat format : formats) {
				text.append(format.asTextFormat());
			}
		} else {
			for (ChatComponentFormat format : formats) {
				if (!parentFormats.contains(format)) {
					text.append(format.asTextFormat());
				}
			}
		}
		text.append(this.getLegacyText(color, formats));
		List<ChatComponent> extra = this.extra;
		if (extra != null) for (ChatComponent component : extra) {
			text.append(component.toLegacyText(color, formats));
		}
		reset = !color.equals(parentColor);
		if (!reset) for (ChatComponentFormat format : formats) {
			if (!parentFormats.contains(format)) {
				reset = true;
				break;
			}
		}
		if (reset) {
			text.append(parentColor.asFormat());
			for (ChatComponentFormat format : parentFormats) {
				text.append(format);
			}
		} else {
			for (ChatComponentFormat format : parentFormats) {
				if (!formats.contains(format)) {
					text.append(format);
				}
			}
		}
		return text.toString();
	}

	protected abstract String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats);

	public ChatTextColor getColor() {
		ChatTextColor color = this.color;
		if (color != null) {
			return color;
		} else {
			ChatComponent parent = this.parent;
			return parent != null ? parent.getColor() : ChatNamedColor.RESET;
		}
	}

	public ChatTextColor getColorRaw() {
		return this.color;
	}

	public boolean isBold() {
		Boolean bold = this.bold;
		if (bold != null) {
			return bold;
		} else {
			ChatComponent parent = this.parent;
			return parent != null && parent.isBold();
		}
	}

	public Boolean isBoldRaw() {
		return this.bold;
	}

	public boolean isItalic() {
		Boolean italic = this.italic;
		if (italic != null) {
			return italic;
		} else {
			ChatComponent parent = this.parent;
			return parent != null && parent.isItalic();
		}
	}

	public Boolean isItalicRaw() {
		return this.italic;
	}

	public boolean isUnderlined() {
		Boolean underlined = this.underlined;
		if (underlined != null) {
			return underlined;
		} else {
			ChatComponent parent = this.parent;
			return parent != null && parent.isUnderlined();
		}
	}

	public Boolean isUnderlinedRaw() {
		return this.underlined;
	}

	public boolean isStrikethrough() {
		Boolean strikethrough = this.strikethrough;
		if (strikethrough != null) {
			return strikethrough;
		} else {
			ChatComponent parent = this.parent;
			return parent != null && parent.isStrikethrough();
		}
	}

	public Boolean isStrikethroughRaw() {
		return this.strikethrough;
	}

	public boolean isObfuscated() {
		Boolean obfuscated = this.obfuscated;
		if (obfuscated != null) {
			return obfuscated;
		} else {
			ChatComponent parent = this.parent;
			return parent != null && parent.isObfuscated();
		}
	}

	public Boolean isObfuscatedRaw() {
		return this.obfuscated;
	}

	public Map<ChatComponentFormat, Boolean> getFormatsRaw() {
		Map<ChatComponentFormat, Boolean> map = new HashMap<>();
		map.put(ChatComponentFormat.BOLD, this.bold);
		map.put(ChatComponentFormat.ITALIC, this.italic);
		map.put(ChatComponentFormat.UNDERLINED, this.underlined);
		map.put(ChatComponentFormat.STRIKETHROUGH, this.strikethrough);
		map.put(ChatComponentFormat.OBFUSCATED, this.obfuscated);
		return map;
	}

	public String getInsertion() {
		return this.insertion;
	}

	public ChatClickEvent getClickEvent() {
		return this.clickEvent;
	}

	public ChatHoverEvent<?> getHoverEvent() {
		return this.hoverEvent;
	}

	public List<ChatComponent> getExtra() {
		List<ChatComponent> extra = this.extra;
		return extra != null ? Collections.unmodifiableList(extra) : null;
	}

	public Boolean getFormatRaw(ChatComponentFormat format) {
		switch (format) {
			case BOLD: return this.isBoldRaw();
			case ITALIC: return this.isItalicRaw();
			case UNDERLINED: return this.isUnderlinedRaw();
			case STRIKETHROUGH: return this.isStrikethroughRaw();
			case OBFUSCATED: return this.isObfuscatedRaw();
			default: throw new IllegalArgumentException();
		}
	}

	public void setColor(ChatTextColor color) {
		this.color = color;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;
	}

	public void setItalic(Boolean italic) {
		this.italic = italic;
	}

	public void setUnderlined(Boolean underlined) {
		this.underlined = underlined;
	}

	public void setStrikethrough(Boolean strikethrough) {
		this.strikethrough = strikethrough;
	}

	public void setObfuscated(Boolean obfuscated) {
		this.obfuscated = obfuscated;
	}

	public void setInsertion(String insertion) {
		this.insertion = insertion;
	}

	public void setClickEvent(ChatClickEvent clickEvent) {
		this.clickEvent = clickEvent;
	}

	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		this.hoverEvent = hoverEvent;
	}

	public void setExtra(Collection<ChatComponent> children) {
		if (children == null) {
			this.extra = null;
		} else {
			this.extra = new ArrayList<>(children);
		}
	}

	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		switch (format) {
			case BOLD:
				this.setBold(isSet);
				break;
			case ITALIC:
				this.setItalic(isSet);
				break;
			case UNDERLINED:
				this.setUnderlined(isSet);
				break;
			case STRIKETHROUGH:
				this.setStrikethrough(isSet);
				break;
			case OBFUSCATED:
				this.setObfuscated(isSet);
				break;
			default: throw new IllegalArgumentException();
		}
	}

	public void append(ChatComponent component) {
		if (component == this) throw new IllegalArgumentException("This component cannot be added");
		if (component.parent != null) throw new IllegalArgumentException("Component already has parent");
		List<ChatComponent> extra = this.extra;
		if (extra == null) {
			extra = this.extra = new ArrayList<>();
		}
		component.parent = this;
		extra.add(component);
	}

	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return new ChatComponentWithLegacyText(this, getLegacyText);
	}

	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return new ChatComponentWithLegacyText(this, legacyText);
	}
}