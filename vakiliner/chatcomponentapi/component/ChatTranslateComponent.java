package vakiliner.chatcomponentapi.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.util.Utils;

public class ChatTranslateComponent extends ChatComponent {
	private String key;
	private final List<ChatComponent> with = new ArrayList<>();
	private String legacyText;

	public ChatTranslateComponent(String legacyText, String key, ChatComponent... with) {
		this(legacyText, key, Arrays.asList(with));
	}

	public ChatTranslateComponent(String legacyText, String key, ChatTextColor color, ChatComponent... with) {
		this(legacyText, key, color, Arrays.asList(with));
	}

	public ChatTranslateComponent(String legacyText, String key, Collection<ChatComponent> with) {
		this.key = Objects.requireNonNull(key);
		this.with.addAll(with);
		this.legacyText = legacyText;
	}

	public ChatTranslateComponent(String legacyText, String key, ChatTextColor color, Collection<ChatComponent> with) {
		super(color);
		this.key = Objects.requireNonNull(key);
		this.with.addAll(with);
		this.legacyText = legacyText;
	}

	public ChatTranslateComponent(ChatTranslateComponent component) {
		super(component);
		this.key = component.key;
		this.with.addAll(component.with);
		this.legacyText = component.legacyText;
	}

	public ChatTranslateComponent clone() {
		return new ChatTranslateComponent(this);
	}

	public String getKey() {
		return this.key;
	}

	public List<ChatComponent> getWith() {
		return Collections.unmodifiableList(this.with);
	}

	public void setKey(String key) {
		this.key = Objects.requireNonNull(key);
	}

	public void setWith(Collection<ChatComponent> with) {
		this.with.clear();
		this.with.addAll(with);
	}

	public void setLegacyText(String legacyText) {
		this.legacyText = legacyText;
	}

	public void addWith(ChatComponent with) {
		this.with.add(with);
	}

	public String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		List<ChatComponent> with = this.with;
		if (with.isEmpty()) {
			return this.legacyText;
		} else {
			return Utils.stringFormat(this.legacyText, with.stream().map((component) -> component.toLegacyText(parentColor, parentFormats)).toArray());
		}
	}
}