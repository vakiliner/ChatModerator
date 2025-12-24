package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatComponentWithLegacyText extends ChatComponentModified {
	private final Supplier<String> getLegacyText;
	private String legacyText;

	public ChatComponentWithLegacyText(ChatComponent component, String legacyText) {
		super(component);
		this.getLegacyText = null;
		this.legacyText = legacyText;
	}

	public ChatComponentWithLegacyText(ChatComponent component, Supplier<String> getLegacyText) {
		super(component);
		this.getLegacyText = Objects.requireNonNull(getLegacyText);
	}

	public ChatComponentWithLegacyText(ChatComponentWithLegacyText component) {
		super(component);
		this.getLegacyText = component.getLegacyText;
		this.legacyText = component.legacyText;
	}

	public String getLegacyText() {
		if (this.legacyText != null) {
			return this.legacyText;
		}
		synchronized (this) {
			if (this.legacyText != null) {
				return this.legacyText;
			}
			return this.legacyText = this.getLegacyText.get();
		}
	}

	public ChatComponent clone() {
		return new ChatComponentWithLegacyText(this);
	}

	public String toLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return Objects.requireNonNull(this.getLegacyText());
	}
}