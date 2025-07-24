package vakiliner.chatcomponentapi.component;

import java.util.Map;
import java.util.Objects;
import com.google.common.collect.Maps;

public class ChatClickEvent {
	private final Action action;
	private final String value;

	public ChatClickEvent(Action action, String value) {
		this.action = Objects.requireNonNull(action);
		this.value = Objects.requireNonNull(value);
	}

	public ChatClickEvent(ChatClickEvent event) {
		this.action = event.action;
		this.value = event.value;
	}

	public Action getAction() {
		return this.action;
	}

	public String getValue() {
		return this.value;
	}

	public ChatClickEvent clone() {
		return new ChatClickEvent(this);
	}

	public static enum Action {
		OPEN_URL("open_url"),
		OPEN_FILE("open_file", false),
		RUN_COMMAND("run_command"),
		SUGGEST_COMMAND("suggest_command"),
		CHANGE_PAGE("change_page"),
		COPY_TO_CLIPBOARD("copy_to_clipboard");

		private static final Map<String, Action> BY_NAME = Maps.newHashMap();
		private final String name;
		private final boolean allowFromServer;

		private Action(String name) {
			this(name, true);
		}

		private Action(String name, boolean allowFromServer) {
			this.name = name;
			this.allowFromServer = allowFromServer;
		}

		public String getName() {
			return this.name;
		}

		public boolean isAllowFromServer() {
			return this.allowFromServer;
		}

		public static Action getByName(String name) {
			return BY_NAME.get(name);
		}

		static {
			for (Action action : values()) {
				BY_NAME.put(action.name, action);
			}
		}
	}
}