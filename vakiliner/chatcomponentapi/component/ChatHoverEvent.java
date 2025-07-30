package vakiliner.chatcomponentapi.component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import com.google.common.collect.Maps;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.gson.APIGson;

public class ChatHoverEvent<V> {
	private final Action<V> action;
	private final V contents;

	public ChatHoverEvent(Action<V> action, V contents) {
		this.action = Objects.requireNonNull(action);
		this.contents = Objects.requireNonNull(contents);
	}

	public ChatHoverEvent(ChatHoverEvent<V> event) {
		this.action = event.action;
		this.contents = event.contents;
	}

	public Action<V> getAction() {
		return this.action;
	}

	public V getContents() {
		return this.contents;
	}

	@Deprecated
	public ChatComponent getValue() {
		if (this.action == Action.SHOW_TEXT) {
			return (ChatComponent) this.contents;
		} else {
			return new ChatTextComponent(APIGson.builder(true).create().toJson(this.contents));
		}
	}

	public ChatHoverEvent<V> clone() {
		return new ChatHoverEvent<>(this);
	}

	public static class Action<V> {
		private static final Map<String, Action<?>> BY_NAME = Maps.newHashMap();
		public static final Action<ChatComponent> SHOW_TEXT = new Action<>("show_text", ChatComponent.class);
		public static final Action<ShowEntity> SHOW_ENTITY = new Action<>("show_entity", ShowEntity.class);
		public static final Action<ShowItem> SHOW_ITEM = new Action<>("show_item", ShowItem.class);
		private final String name;
		private final Class<V> type;

		private Action(String name, Class<V> type) {
			this.name = name;
			this.type = type;
			BY_NAME.put(this.name, this);
		}

		public String getName() {
			return this.name;
		}

		public Class<V> getType() {
			return this.type;
		}

		public static Action<?> getByName(String name) {
			return BY_NAME.get(name);
		}
	}

	public static class ShowEntity {
		private final ChatId type;
		private final UUID uuid;
		private final ChatComponent name;

		public ShowEntity(ChatOfflinePlayer player) {
			this.type = new ChatId("minecraft", "player");
			this.uuid = player.getUniqueId();
			this.name = new ChatTextComponent(player.getName());
		}

		public ShowEntity(ChatId type, UUID uuid, ChatComponent name) {
			this.type = type;
			this.uuid = uuid;
			this.name = name;
		}

		public ChatId getType() {
			return this.type;
		}

		public UUID getUniqueId() {
			return this.uuid;
		}

		public ChatComponent getName() {
			return this.name;
		}
	}

	public static class ShowItem {
		private final ChatId item;
		private final Integer count;

		public ShowItem(ChatId item, Integer count) {
			this.item = item;
			this.count = count;
		}

		public ChatId getItem() {
			return this.item;
		}

		public Integer getCount() {
			return this.count;
		}
	}
}