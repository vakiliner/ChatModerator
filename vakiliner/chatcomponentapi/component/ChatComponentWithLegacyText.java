package vakiliner.chatcomponentapi.component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatComponentWithLegacyText extends ChatComponent {
	private final ChatComponent component;
	private final Supplier<String> getLegacyText;

	public ChatComponentWithLegacyText(ChatComponent component, String legacyText) {
		this(component, () -> legacyText);
	}

	public ChatComponentWithLegacyText(ChatComponent component, Supplier<String> getLegacyText) {
		if (component instanceof ChatComponentWithLegacyText) throw new IllegalArgumentException("ChatComponentWithLegacyText cannot be used as a component");
		this.component = Objects.requireNonNull(component);
		this.getLegacyText = Objects.requireNonNull(getLegacyText);
	}

	public ChatComponentWithLegacyText(ChatComponentWithLegacyText component) {
		this.component = component.component;
		this.getLegacyText = component.getLegacyText;
	}

	public Supplier<String> getLegacyText() {
		return this.getLegacyText;
	}

	public ChatComponent getComponent() {
		return this.component;
	}

	public ChatComponent clone() {
		return new ChatComponentWithLegacyText(this);
	}

	public String toLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return Objects.requireNonNull(this.getLegacyText.get());
	}

	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return this.component.withLegacyText(legacyText);
	}

	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return this.component.withLegacyText(getLegacyText);
	}

	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		throw new UnsupportedOperationException();
	}

	public ChatTextColor getColor() {
		throw new UnsupportedOperationException();
	}

	public ChatTextColor getColorRaw() {
		throw new UnsupportedOperationException();
	}

	public boolean isBold() {
		throw new UnsupportedOperationException();
	}

	public Boolean isBoldRaw() {
		throw new UnsupportedOperationException();
	}

	public boolean isItalic() {
		throw new UnsupportedOperationException();
	}

	public Boolean isItalicRaw() {
		throw new UnsupportedOperationException();
	}

	public boolean isUnderlined() {
		throw new UnsupportedOperationException();
	}

	public Boolean isUnderlinedRaw() {
		throw new UnsupportedOperationException();
	}

	public boolean isStrikethrough() {
		throw new UnsupportedOperationException();
	}

	public Boolean isStrikethroughRaw() {
		throw new UnsupportedOperationException();
	}

	public boolean isObfuscated() {
		throw new UnsupportedOperationException();
	}

	public Boolean isObfuscatedRaw() {
		throw new UnsupportedOperationException();
	}

	public Map<ChatComponentFormat, Boolean> getFormatsRaw() {
		throw new UnsupportedOperationException();
	}

	public String getInsertion() {
		throw new UnsupportedOperationException();
	}

	public ChatClickEvent getClickEvent() {
		throw new UnsupportedOperationException();
	}

	public ChatHoverEvent<?> getHoverEvent() {
		throw new UnsupportedOperationException();
	}

	public List<ChatComponent> getExtra() {
		throw new UnsupportedOperationException();
	}

	public Boolean getFormatRaw(ChatComponentFormat format) {
		throw new UnsupportedOperationException();
	}

	public void setColor(ChatTextColor color) {
		throw new UnsupportedOperationException();
	}

	public void setBold(Boolean bold) {
		throw new UnsupportedOperationException();
	}

	public void setItalic(Boolean italic) {
		throw new UnsupportedOperationException();
	}

	public void setUnderlined(Boolean underlined) {
		throw new UnsupportedOperationException();
	}

	public void setStrikethrough(Boolean strikethrough) {
		throw new UnsupportedOperationException();
	}

	public void setObfuscated(Boolean obfuscated) {
		throw new UnsupportedOperationException();
	}

	public void setInsertion(String insertion) {
		throw new UnsupportedOperationException();
	}

	public void setClickEvent(ChatClickEvent clickEvent) {
		throw new UnsupportedOperationException();
	}

	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		throw new UnsupportedOperationException();
	}

	public void setExtra(Collection<ChatComponent> children) {
		throw new UnsupportedOperationException();
	}

	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		throw new UnsupportedOperationException();
	}

	public void append(ChatComponent component) {
		throw new UnsupportedOperationException();
	}
}