package vakiliner.chatcomponentapi.component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

	public ChatComponent getComponent(boolean isConsole) {
		return this.getComponent();
	}

	@Override
	public String toLegacyText() {
		return this.component.toLegacyText();
	}

	@Override
	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.component.getLegacyText(parentColor, parentFormats);
	}

	@Override
	public ChatStyle getStyle() {
		return this.component.getStyle();
	}

	@Deprecated
	@Override
	public ChatTextColor getColorRaw() {
		return this.component.getColorRaw();
	}

	@Deprecated
	@Override
	public Boolean isBoldRaw() {
		return this.component.isBoldRaw();
	}

	@Deprecated
	@Override
	public Boolean isItalicRaw() {
		return this.component.isItalicRaw();
	}

	@Deprecated
	@Override
	public Boolean isUnderlinedRaw() {
		return this.component.isUnderlinedRaw();
	}

	@Deprecated
	@Override
	public Boolean isStrikethroughRaw() {
		return this.component.isStrikethroughRaw();
	}

	@Deprecated
	@Override
	public Boolean isObfuscatedRaw() {
		return this.component.isObfuscatedRaw();
	}

	@Deprecated
	@Override
	public String getInsertion() {
		return this.component.getInsertion();
	}

	@Deprecated
	@Override
	public ChatClickEvent getClickEvent() {
		return this.component.getClickEvent();
	}

	@Deprecated
	@Override
	public ChatHoverEvent<?> getHoverEvent() {
		return this.component.getHoverEvent();
	}

	@Override
	public List<ChatComponent> getExtra() {
		return this.component.getExtra();
	}

	@Deprecated
	@Override
	public Boolean getFormatRaw(ChatComponentFormat format) {
		return this.component.getFormatRaw(format);
	}

	@Deprecated
	@Override
	public Map<ChatComponentFormat, Boolean> getFormatsRaw() {
		return this.component.getFormatsRaw();
	}

	@Override
	public void setStyle(ChatStyle style) {
		this.component.setStyle(style);
	}

	@Deprecated
	@Override
	public void setColor(ChatTextColor color) {
		this.component.setColor(color);
	}

	@Deprecated
	@Override
	public void setBold(Boolean bold) {
		this.component.setBold(bold);
	}

	@Deprecated
	@Override
	public void setItalic(Boolean italic) {
		this.component.setItalic(italic);
	}

	@Deprecated
	@Override
	public void setUnderlined(Boolean underlined) {
		this.component.setUnderlined(underlined);
	}

	@Deprecated
	@Override
	public void setStrikethrough(Boolean strikethrough) {
		this.component.setStrikethrough(strikethrough);
	}

	@Deprecated
	@Override
	public void setObfuscated(Boolean obfuscated) {
		this.component.setObfuscated(obfuscated);
	}

	@Deprecated
	@Override
	public void setInsertion(String insertion) {
		this.component.setInsertion(insertion);
	}

	@Deprecated
	@Override
	public void setClickEvent(ChatClickEvent clickEvent) {
		this.component.setClickEvent(clickEvent);
	}

	@Deprecated
	@Override
	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		this.component.setHoverEvent(hoverEvent);
	}

	@Deprecated
	@Override
	public void setExtra(Collection<ChatComponent> children) {
		this.component.setExtra(children);
	}

	@Deprecated
	@Override
	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		this.component.setFormat(format, isSet);
	}

	@Deprecated
	@Override
	public void setFormats(Map<ChatComponentFormat, Boolean> map) {
		this.component.setFormats(map);
	}

	@Override
	public void append(ChatComponent component) {
		this.component.append(component);
	}

	@Override
	protected void unsafeAppend(ChatComponent component) {
		this.component.unsafeAppend(component);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyComponent(Supplier<ChatComponent> getLegacyComponent) {
		return this.component.withLegacyComponent(getLegacyComponent);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyComponent(ChatComponent legacyComponent) {
		return this.component.withLegacyComponent(legacyComponent);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return this.component.withLegacyText(getLegacyText);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return this.component.withLegacyText(legacyText);
	}

	@Override
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

	@Override
	protected void serialize(JsonObject object) {
		this.component.serialize(object);
	}
}
