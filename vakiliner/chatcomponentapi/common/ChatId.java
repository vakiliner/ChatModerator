package vakiliner.chatcomponentapi.common;

import java.util.Objects;

public class ChatId {
	private final String namespace;
	private final String value;

	@Deprecated
	public ChatId(String string) {
		int index = string.indexOf(':');
		this.namespace = index > 0 ? string.substring(0, index) : "minecraft";
		this.value = index >= 0 ? string.substring(index + 1) : string;
	}

	public ChatId(String namespace, String value) {
		this.namespace = Objects.requireNonNull(namespace);
		this.value = Objects.requireNonNull(value);
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getValue() {
		return this.value;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatId)) {
			return false;
		} else {
			ChatId other = (ChatId) obj;
			return this.namespace.equals(other.namespace) && this.value.equals(other.value);
		}
	}

	public String toString() {
		return this.namespace + ':' + this.value;
	}

	public static ChatId parse(String string) {
		return parse(string, "minecraft");
	}

	public static ChatId parse(String string, String defaultNamespace) {
		int index = string.indexOf(':');
		String namespace = index > 0 ? string.substring(0, index) : defaultNamespace;
		String value = index >= 0 ? string.substring(index + 1) : string;
		return new ChatId(namespace, value);
	}
}