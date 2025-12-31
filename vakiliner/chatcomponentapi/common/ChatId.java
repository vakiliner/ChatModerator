package vakiliner.chatcomponentapi.common;

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
		this.namespace = namespace;
		this.value = value;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getValue() {
		return this.value;
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