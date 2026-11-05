package vakiliner.chatcomponentapi.common;

import static java.util.Objects.requireNonNull;

public final class ChatId {
	private static final String MINECRAFT = "minecraft";
	private final String namespace;
	private final String value;

	@Deprecated
	public ChatId(String string) {
		int index = string.indexOf(':');
		this.namespace = index > 0 ? string.substring(0, index) : "minecraft";
		this.value = index >= 0 ? string.substring(index + 1) : string;
	}

	public ChatId(String namespace, String value) {
		this.namespace = requireNonNull(namespace);
		this.value = requireNonNull(value);
	}

	public String namespace() {
		return this.namespace;
	}

	public String value() {
		return this.value;
	}

	@Deprecated
	public String getNamespace() {
		return this.namespace();
	}

	@Deprecated
	public String getValue() {
		return this.value();
	}

	@Override
	public String toString() {
		return this.namespace + ':' + this.value;
	}

	@Override
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

	public static ChatId minecraft(String value) {
		return new ChatId(MINECRAFT, value);
	}

	public static ChatId fromString(String string) {
		return fromString(string, MINECRAFT);
	}

	public static ChatId fromString(String string, String defaultNamespace) {
		int index = string.indexOf(':');
		String namespace = index > 0 ? string.substring(0, index) : defaultNamespace;
		String value = index >= 0 ? string.substring(index + 1) : string;
		return new ChatId(namespace, value);
	}

	@Deprecated
	public static ChatId of(String string) {
		return fromString(string);
	}

	@Deprecated
	public static ChatId of(String string, String defaultNamespace) {
		return fromString(string, defaultNamespace);
	}

	@Deprecated
	public static ChatId parse(String string) {
		return fromString(string);
	}

	@Deprecated
	public static ChatId parse(String string, String defaultNamespace) {
		return fromString(string, defaultNamespace);
	}
}
