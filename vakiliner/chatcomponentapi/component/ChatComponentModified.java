package vakiliner.chatcomponentapi.component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public abstract class ChatComponentModified extends ChatComponent {
	protected final ChatComponent component;

	public ChatComponentModified(ChatComponent component) {
		if (component instanceof ChatComponentModified) {
			throw new IllegalArgumentException(component.getClass().getSimpleName() + " cannot be used as a component");
		}
		this.component = Objects.requireNonNull(component);
	}

	protected ChatComponentModified(ChatComponentModified component) {
		this.component = component.component.clone();
	}

	public final ChatComponent getComponent() {
		return this.component;
	}

	public String toLegacyText() {
		return this.component.toLegacyText();
	}

	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.component.getLegacyText(parentColor, parentFormats);
	}

	public ChatTextColor getColor() {
		return this.component.getColor();
	}

	public Boolean isBoldRaw() {
		return this.component.isBoldRaw();
	}

	public Boolean isItalicRaw() {
		return this.component.isItalicRaw();
	}

	public Boolean isUnderlinedRaw() {
		return this.component.isUnderlinedRaw();
	}

	public Boolean isStrikethroughRaw() {
		return this.component.isStrikethroughRaw();
	}

	public Boolean isObfuscatedRaw() {
		return this.component.isObfuscatedRaw();
	}

	public String getInsertion() {
		return this.component.getInsertion();
	}

	public ChatClickEvent getClickEvent() {
		return this.component.getClickEvent();
	}

	public ChatHoverEvent<?> getHoverEvent() {
		return this.component.getHoverEvent();
	}

	public List<ChatComponent> getExtra() {
		return this.component.getExtra();
	}

	public void setColor(ChatTextColor color) {
		this.component.setColor(color);
	}

	public void setBold(Boolean bold) {
		this.component.setBold(bold);
	}

	public void setItalic(Boolean italic) {
		this.component.setItalic(italic);
	}

	public void setUnderlined(Boolean underlined) {
		this.component.setUnderlined(underlined);
	}

	public void setStrikethrough(Boolean strikethrough) {
		this.component.setStrikethrough(strikethrough);
	}

	public void setObfuscated(Boolean obfuscated) {
		this.component.setObfuscated(obfuscated);
	}

	public void setInsertion(String insertion) {
		this.component.setInsertion(insertion);
	}

	public void setClickEvent(ChatClickEvent clickEvent) {
		this.component.setClickEvent(clickEvent);
	}

	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		this.component.setHoverEvent(hoverEvent);
	}

	public void setExtra(Collection<ChatComponent> children) {
		this.component.setExtra(children);
	}

	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		this.component.setFormat(format, isSet);
	}

	public void append(ChatComponent component) {
		this.component.append(component);
	}

	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return this.component.withLegacyText(legacyText);
	}

	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return this.component.withLegacyText(getLegacyText);
	}
}