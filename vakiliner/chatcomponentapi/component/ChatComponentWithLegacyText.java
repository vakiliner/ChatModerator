package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatComponentWithLegacyText extends ChatComponentModified {
	private final Supplier<ChatComponent> getLegacyComponent;
	private ChatComponent legacyComponent;

	public ChatComponentWithLegacyText(ChatComponent component, Supplier<ChatComponent> getLegacyComponent) {
		super(component);
		this.getLegacyComponent = Objects.requireNonNull(getLegacyComponent);
	}

	public ChatComponentWithLegacyText(ChatComponent component, ChatComponent legacyComponent) {
		this(component, () -> legacyComponent);
		this.setLegacyComponent(legacyComponent);
	}

	public ChatComponentWithLegacyText(ChatComponent component, String legacyText) {
		this(component, new ChatTextComponent(legacyText));
	}

	public ChatComponentWithLegacyText(ChatComponentWithLegacyText component) {
		super(component);
		this.getLegacyComponent = component.getLegacyComponent;
		ChatComponent legacyComponent = component.legacyComponent;
		if (legacyComponent != null) {
			this.setLegacyComponent(legacyComponent.clone());
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
			return this.setLegacyComponent(this.getLegacyComponent.get());
		}
	}

	private ChatComponent setLegacyComponent(ChatComponent legacyComponent) {
		this.legacyComponent = Objects.requireNonNull(legacyComponent);
		this.legacyComponent.setParent(this);
		return this.legacyComponent;
	}

	public ChatComponent clone() {
		return new ChatComponentWithLegacyText(this);
	}

	public String toLegacyText() {
		return this.legacyComponent.toLegacyText();
	}

	public String toLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.legacyComponent.toLegacyText(parentColor, parentFormats);
	}
}