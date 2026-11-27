package vakiliner.chatcomponentapi.component;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.Set;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatSelectorComponent extends ChatComponent {
	private String selector;
	private ChatComponent separator;

	@Deprecated
	public ChatSelectorComponent() {
		this("");
	}

	@Deprecated
	public ChatSelectorComponent(ChatTextColor color) {
		this("", color);
	}

	public ChatSelectorComponent(String selector) {
		this.selector = requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatComponent separator) {
		this(selector);
		this.separator = separator;
	}

	public ChatSelectorComponent(String selector, ChatStyle style) {
		super(style);
		this.selector = requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatTextColor color) {
		super(color);
		this.selector = requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatComponentFormat format) {
		super(format);
		this.selector = requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatComponent separator, ChatStyle style) {
		this(selector, style);
		this.separator = separator;
	}

	public ChatSelectorComponent(String selector, ChatComponent separator, ChatTextColor color) {
		this(selector, color);
		this.separator = separator;
	}

	public ChatSelectorComponent(String selector, ChatComponent separator, ChatComponentFormat format) {
		this(selector, format);
		this.separator = separator;
	}

	@Deprecated
	public ChatSelectorComponent(ChatSelectorComponent component, boolean cloneExtra) {
		super(component, cloneExtra);
		this.selector = component.selector;
		this.separator = component.separator;
	}

	@Deprecated
	@Override
	public ChatSelectorComponent clone(boolean cloneExtra) {
		return new ChatSelectorComponent(this, cloneExtra);
	}

	@Override
	public ChatSelectorComponent copy() {
		return new ChatSelectorComponent(this, false);
	}

	public String getSelector() {
		return this.selector;
	}

	public ChatComponent getSeparator() {
		return this.separator;
	}

	public void setSelector(String selector) {
		this.selector = requireNonNull(selector);
	}

	public void setSeparator(ChatComponent separator) {
		this.separator = separator;
	}

	@Override
	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.selector;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatSelectorComponent)) {
			return false;
		} else {
			ChatSelectorComponent other = (ChatSelectorComponent) obj;
			return super.equals(other) && this.selector.equals(other.selector) && Objects.equals(this.separator, other.separator);
		}
	}

	@Override
	protected void serialize(JsonObject object) {
		object.addProperty("selector", this.selector);
		ChatComponent separator = this.separator;
		if (separator != null) {
			object.add("separator", ChatComponent.serialize(separator));
		}
	}

	public static ChatSelectorComponent deserialize(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		JsonElement rawSeparator = object.get("separator");
		String selector = object.get("selector").getAsString();
		ChatComponent separator = rawSeparator != null ? ChatComponent.deserialize(rawSeparator) : null;
		return ChatComponent.deserialize((style) -> new ChatSelectorComponent(selector, separator, style), object);
	}
}
