package vakiliner.chatcomponentapi.component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
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
		this.component = component.component;
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

	@Deprecated
	public ChatTextColor getColorRaw() {
		return this.component.getColorRaw();
	}

	@Deprecated
	public Boolean isBoldRaw() {
		return this.component.isBoldRaw();
	}

	@Deprecated
	public Boolean isItalicRaw() {
		return this.component.isItalicRaw();
	}

	@Deprecated
	public Boolean isUnderlinedRaw() {
		return this.component.isUnderlinedRaw();
	}

	@Deprecated
	public Boolean isStrikethroughRaw() {
		return this.component.isStrikethroughRaw();
	}

	@Deprecated
	public Boolean isObfuscatedRaw() {
		return this.component.isObfuscatedRaw();
	}

	@Deprecated
	public String getInsertion() {
		return this.component.getInsertion();
	}

	@Deprecated
	public ChatClickEvent getClickEvent() {
		return this.component.getClickEvent();
	}

	@Deprecated
	public ChatHoverEvent<?> getHoverEvent() {
		return this.component.getHoverEvent();
	}

	public List<ChatComponent> getExtra() {
		return this.component.getExtra();
	}

	@Deprecated
	public void setColor(ChatTextColor color) {
		this.component.setColor(color);
	}

	@Deprecated
	public void setBold(Boolean bold) {
		this.component.setBold(bold);
	}

	@Deprecated
	public void setItalic(Boolean italic) {
		this.component.setItalic(italic);
	}

	@Deprecated
	public void setUnderlined(Boolean underlined) {
		this.component.setUnderlined(underlined);
	}

	@Deprecated
	public void setStrikethrough(Boolean strikethrough) {
		this.component.setStrikethrough(strikethrough);
	}

	@Deprecated
	public void setObfuscated(Boolean obfuscated) {
		this.component.setObfuscated(obfuscated);
	}

	@Deprecated
	public void setInsertion(String insertion) {
		this.component.setInsertion(insertion);
	}

	@Deprecated
	public void setClickEvent(ChatClickEvent clickEvent) {
		this.component.setClickEvent(clickEvent);
	}

	@Deprecated
	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		this.component.setHoverEvent(hoverEvent);
	}

	public void setExtra(Collection<ChatComponent> children) {
		this.component.setExtra(children);
	}

	@Deprecated
	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		this.component.setFormat(format, isSet);
	}

	protected void unsafeAppend(ChatComponent component) {
		this.component.unsafeAppend(component);
	}

	public void append(ChatComponent component) {
		this.component.append(component);
	}

	public ChatComponentWithLegacyText withLegacyComponent(Supplier<ChatComponent> getLegacyComponent) {
		return this.component.withLegacyComponent(getLegacyComponent);
	}

	public ChatComponentWithLegacyText withLegacyComponent(ChatComponent legacyComponent) {
		return this.component.withLegacyComponent(legacyComponent);
	}

	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return this.component.withLegacyText(getLegacyText);
	}

	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return this.component.withLegacyText(legacyText);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatComponentModified)) {
			return false;
		} else {
			ChatComponentModified other = (ChatComponentModified) obj;
			return this.component.equals(other.component);
		}
	}

	protected void serialize(JsonObject object) {
		this.component.serialize(object);
	}
}