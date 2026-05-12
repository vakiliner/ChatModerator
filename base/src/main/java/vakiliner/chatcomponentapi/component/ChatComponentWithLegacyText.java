package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatComponentWithLegacyText extends ChatComponentModified {
	private final Supplier<ChatComponent> getLegacyComponent;
	private ChatComponent legacyComponent;

	@Deprecated
	public ChatComponentWithLegacyText(ChatComponent component, Supplier<ChatComponent> getLegacyComponent) {
		super(component);
		this.getLegacyComponent = Objects.requireNonNull(getLegacyComponent);
	}

	@Deprecated
	public ChatComponentWithLegacyText(ChatComponent component, ChatComponent legacyComponent) {
		super(component);
		this.getLegacyComponent = null;
		this.legacyComponent = Objects.requireNonNull(legacyComponent);
	}

	@Deprecated
	public ChatComponentWithLegacyText(ChatComponent component, String legacyText) {
		this(component, new ChatTextComponent(legacyText));
	}

	protected ChatComponentWithLegacyText(ChatComponentWithLegacyText component) {
		super(component);
		this.getLegacyComponent = component.getLegacyComponent;
		this.legacyComponent = component.legacyComponent;
	}

	public ChatComponent clone(boolean cloneExtra) {
		return new ChatComponentWithLegacyText(this);
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

	public ChatComponent getComponent(boolean isConsole) {
		return isConsole ? this.getLegacyComponent() : super.getComponent();
	}

	public String toLegacyText() {
		return this.getLegacyComponent().toLegacyText();
	}

	public String toLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.getLegacyComponent().toLegacyText(parentColor, parentFormats);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatComponentWithLegacyText)) {
			return false;
		} else {
			ChatComponentWithLegacyText other = (ChatComponentWithLegacyText) obj;
			return super.equals(other) && this.getLegacyComponent().equals(other.getLegacyComponent());
		}
	}
}