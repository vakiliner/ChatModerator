package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatSelectorComponent extends ChatComponent {
	private String selector;

	public ChatSelectorComponent() {
		this("");
	}

	public ChatSelectorComponent(ChatTextColor color) {
		this("", color);
	}

	public ChatSelectorComponent(String selector) {
		this.selector = Objects.requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatTextColor color) {
		super(color);
		this.selector = Objects.requireNonNull(selector);
	}

	public ChatSelectorComponent(ChatSelectorComponent component) {
		super(component);
		this.selector = component.selector;
	}

	public ChatSelectorComponent clone() {
		return new ChatSelectorComponent(this);
	}

	public String getSelector() {
		return this.selector;
	}

	public void setSelector(String selector) {
		this.selector = Objects.requireNonNull(selector);
	}

	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.selector;
	}
}