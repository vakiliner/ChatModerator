package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatComponentWithLegacyText extends ChatComponentModified {
	private final Supplier<ChatComponent> getLegacy;
	private ChatComponent legacyComponent;
	@Deprecated
	private String legacyText;

	@Deprecated
	public ChatComponentWithLegacyText(ChatComponent component, String legacyText) {
		super(component);
		this.getLegacy = () -> new ChatTextComponent(this.legacyText);
		this.legacyText = legacyText;
	}

	@Deprecated
	public ChatComponentWithLegacyText(ChatComponent component, Supplier<String> getLegacyText) {
		super(component);
		Objects.requireNonNull(getLegacyText);
		this.getLegacy = () -> new ChatTextComponent(this.legacyText = getLegacyText.get());
	}

	public ChatComponentWithLegacyText(ChatComponentWithLegacyText component) {
		super(component);
		this.getLegacy = component.getLegacy;
		this.legacyComponent = component.legacyComponent;
	}

	@Deprecated
	public String getLegacyText() {
		if (this.legacyText != null) {
			return this.legacyText;
		}
		synchronized (this) {
			if (this.legacyText != null) {
				return this.legacyText;
			}
			ChatComponent lecagyComponent = this.getLegacyComponent();
			if (this.legacyText != null) {
				return this.legacyText;
			}
			return this.legacyText = lecagyComponent.toLegacyText();
		}
	}

	public ChatComponent getLegacyComponent() {
		if (this.legacyComponent != null) {
			return this.legacyComponent;
		}
		synchronized (this) {
			if (this.legacyComponent != null) {
				return this.legacyComponent;
			}
			return this.legacyComponent = this.getLegacy.get();
		}
	}

	public ChatComponent clone() {
		return new ChatComponentWithLegacyText(this);
	}

	public String toLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return Objects.requireNonNull(this.getLegacyText());
	}
}