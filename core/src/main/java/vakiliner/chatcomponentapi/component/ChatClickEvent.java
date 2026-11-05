package vakiliner.chatcomponentapi.component;

import static java.util.Objects.requireNonNull;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.gson.IGsonSerializer;

public class ChatClickEvent implements IGsonSerializer {
	private final Action action;
	private final String value;

	public ChatClickEvent(Action action, String value) {
		this.action = requireNonNull(action);
		this.value = requireNonNull(value);
	}

	public ChatClickEvent(ChatClickEvent event) {
		this.action = event.action;
		this.value = event.value;
	}

	public ChatClickEvent clone() {
		return new ChatClickEvent(this);
	}

	public Action action() {
		return this.action;
	}

	public String value() {
		return this.value;
	}

	@Deprecated
	public Action getAction() {
		return this.action();
	}

	@Deprecated
	public String getValue() {
		return this.value();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatClickEvent)) {
			return false;
		} else {
			ChatClickEvent other = (ChatClickEvent) obj;
			return this.action == other.action && this.value.equals(other.value);
		}
	}

	@Override
	public JsonElement serialize() {
		return serialize(this);
	}

	public static JsonElement serialize(ChatClickEvent event) {
		JsonObject object = new JsonObject();
		object.addProperty("action", event.action.getName());
		object.addProperty("value", event.value);
		return object;
	}

	public static ChatClickEvent deserialize(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		Action action = Action.getByName(object.get("action").getAsString());
		String value = object.get("value").getAsString();
		return new ChatClickEvent(action, value);
	}

	public static enum Action {
		OPEN_URL(),
		OPEN_FILE(false),
		RUN_COMMAND(),
		SUGGEST_COMMAND(),
		CHANGE_PAGE(),
		COPY_TO_CLIPBOARD();

		private static final Map<String, Action> BY_NAME = Maps.newHashMap();
		private final boolean allowFromServer;

		private Action() {
			this(true);
		}

		private Action(boolean allowFromServer) {
			this.allowFromServer = allowFromServer;
		}

		public String getName() {
			return this.name().toLowerCase();
		}

		public boolean isAllowFromServer() {
			return this.allowFromServer;
		}

		public static Action getByName(String name) {
			return BY_NAME.get(name);
		}

		static {
			for (Action action : values()) {
				BY_NAME.put(action.getName(), action);
			}
		}
	}
}
