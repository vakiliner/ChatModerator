package vakiliner.chatcomponentapi.component;

import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatComponentWithLegacyText extends ChatComponentModified {
	private final Supplier<ChatComponent> getLegacyComponent;
	private ChatComponent legacyComponent;

	public ChatComponentWithLegacyText(ChatComponent component, Supplier<ChatComponent> getLegacyComponent) {
		super(component);
		this.getLegacyComponent = getLegacyComponent;
	}

	public ChatComponentWithLegacyText(ChatComponent component, ChatComponent legacyComponent) {
		this(component, () -> legacyComponent);
		this.legacyComponent = legacyComponent;
	}

	public ChatComponentWithLegacyText(ChatComponent component, String legacyText) {
		this(component, new ChatTextComponent(legacyText));
	}

	public ChatComponentWithLegacyText(ChatComponentWithLegacyText component) {
		super(component);
		this.getLegacyComponent = component.getLegacyComponent;
		this.legacyComponent = component.legacyComponent;
	}

	public ChatComponent getLegacyComponent() {
		if (this.legacyComponent != null) {
			return this.legacyComponent;
		}
		synchronized (this) {
			if (this.legacyComponent != null) {
				return this.legacyComponent;
			}
			return this.legacyComponent = this.getLegacyComponent.get();
		}
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