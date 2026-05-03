package vakiliner.chatcomponentapi.component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.gson.IGsonSerializer;

public class ChatHoverEvent<V extends ChatHoverEvent.IContent> implements IGsonSerializer {
	private final Action<V> action;
	private final V contents;

	public ChatHoverEvent(Action<V> action, V contents) {
		this.action = Objects.requireNonNull(action);
		this.contents = Objects.requireNonNull(contents);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public ChatHoverEvent(Action<V> action, Object contents) {
		this(action, (V) contents);
	}

	public ChatHoverEvent(ChatHoverEvent<V> event) {
		this.action = event.action;
		this.contents = event.contents;
	}

	public ChatHoverEvent<V> clone() {
		return new ChatHoverEvent<>(this);
	}

	public Action<V> getAction() {
		return this.action;
	}

	public V getContents() {
		return this.contents;
	}

	@SuppressWarnings("unchecked")
	public <T extends IContent> T getValue(Action<T> action) {
		return this.action == action ? (T) this.contents : null;
	}

	@Deprecated
	public ChatComponent getValue() {
		if (this.action == Action.SHOW_TEXT) {
			return (ChatComponent) this.contents;
		} else {
			return new ChatTextComponent(this.contents.serialize(true).toString());
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatHoverEvent)) {
			return false;
		} else {
			ChatHoverEvent<?> other = (ChatHoverEvent<?>) obj;
			return this.action == other.action && this.contents.equals(other.contents);
		}
	}

	public JsonElement serialize() {
		return serialize(this);
	}

	public static JsonElement serialize(ChatHoverEvent<?> event) {
		return serialize(event, false);
	}

	@SuppressWarnings("deprecation")
	public static JsonElement serialize(ChatHoverEvent<?> event, boolean old) {
		JsonObject object = new JsonObject();
		object.addProperty("action", event.action.getName());
		object.add(!old ? "contents" : "value", (!old ? event.getContents() : event.getValue()).serialize());
		return object;
	}

	public static ChatHoverEvent<?> deserialize(JsonElement element) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		Action<?> action = Action.getByName(object.get("action").getAsString());
		JsonElement contents = object.get("contents");
		if (contents != null) {
			if (action == Action.SHOW_TEXT) {
				return new ChatHoverEvent<>(Action.SHOW_TEXT, ChatComponent.deserialize(contents));
			} else if (action == Action.SHOW_ENTITY) {
				return new ChatHoverEvent<>(Action.SHOW_ENTITY, ShowEntity.deserialize(contents));
			} else if (action == Action.SHOW_ITEM) {
				return new ChatHoverEvent<>(Action.SHOW_ITEM, ShowItem.deserialize(contents));
			}
			throw new JsonParseException("Unknown action");
		}
		contents = object.get("value");
		if (contents != null) {
			if (action == Action.SHOW_TEXT) {
				return new ChatHoverEvent<>(Action.SHOW_TEXT, ChatComponent.deserialize(contents));
			}
			contents = new Gson().toJsonTree(ChatTextComponent.deserialize(contents).getText());
			if (action == Action.SHOW_ENTITY) {
				return new ChatHoverEvent<>(Action.SHOW_ENTITY, ShowEntity.deserialize(contents, true));
			} else if (action == Action.SHOW_ITEM) {
				return new ChatHoverEvent<>(Action.SHOW_ITEM, ShowItem.deserialize(contents));
			}
			throw new JsonParseException("Unknown action");
		}
		throw new JsonParseException("No content");
	}

	public static class Action<V extends IContent> {
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

	public static interface IContent extends IGsonSerializer {
		default JsonElement serialize(boolean old) {
			return this.serialize();
		}
	}

	public static class ShowEntity implements IContent {
		private final ChatId type;
		private final UUID id;
		private final ChatComponent name;

		@Deprecated
		public ShowEntity(ChatOfflinePlayer player) {
			this.type = new ChatId("minecraft", "player");
			this.id = player.getUniqueId();
			this.name = new ChatTextComponent(player.getName());
		}

		public ShowEntity(ChatId type, UUID id, ChatComponent name) {
			this.type = Objects.requireNonNull(type);
			this.id = Objects.requireNonNull(id);
			this.name = name;
		}

		public ChatId getType() {
			return this.type;
		}

		public UUID getUniqueId() {
			return this.id;
		}

		public ChatComponent getName() {
			return this.name;
		}

		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			} else if (obj instanceof ShowEntity) {
				ShowEntity other = (ShowEntity) obj;
				return this.type.equals(other.type) && this.id.equals(other.id) && Objects.equals(this.name, other.name);
			} else {
				return false;
			}
		}

		public JsonElement serialize() {
			return serialize(this);
		}

		public JsonElement serialize(boolean old) {
			return serialize(this, old);
		}

		public static JsonElement serialize(ShowEntity showEntity) {
			return serialize(showEntity, false);
		}

		public static JsonElement serialize(ShowEntity showEntity, boolean old) {
			JsonObject object = new JsonObject();
			ChatId type = showEntity.type;
			UUID id = showEntity.id;
			ChatComponent name = showEntity.name;
			if (type != null) object.addProperty("type", type.toString());
			if (id != null) object.addProperty("id", id.toString());
			if (name != null) {
				if (!old) {
					object.add("name", ChatComponent.serialize(name));
				} else {
					object.addProperty("name", ChatComponent.serialize(name).toString());
				}
			}
			return object;
		}

		public static ShowEntity deserialize(JsonElement element) {
			return deserialize(element, false);
		}

		public static ShowEntity deserialize(JsonElement element, boolean old) {
			JsonObject object = element.getAsJsonObject();
			JsonElement rawName = object.get("name");
			if (old) {
				rawName = new Gson().toJsonTree(rawName.getAsString());
			}
			ChatId type = ChatId.parse(object.get("type").getAsString());
			UUID id = UUID.fromString(object.get("id").getAsString());
			ChatComponent name = rawName != null ? ChatComponent.deserialize(rawName) : null;
			return new ShowEntity(type, id, name);
		}
	}

	public static class ShowItem implements IContent {
		private final ChatId id;
		private final int count;

		@Deprecated
		public ShowItem(ChatId item, Integer count) {
			this(item, count.intValue());
		}

		public ShowItem(ChatId id, int count) {
			this.id = Objects.requireNonNull(id);
			this.count = count;
		}

		public ChatId getItem() {
			return this.id;
		}

		public int getCount() {
			return this.count;
		}

		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			} else if (obj instanceof ShowItem) {
				ShowItem other = (ShowItem) obj;
				return this.id.equals(other.id) && this.count == other.count;
			} else {
				return false;
			}
		}

		public JsonElement serialize() {
			return serialize(this);
		}

		public static JsonElement serialize(ShowItem showItem) {
			JsonObject object = new JsonObject();
			object.addProperty("id", showItem.id.toString());
			int count = showItem.count;
			if (count != 1) {
				object.addProperty("count", count);
			}
			return object;
		}

		public static ShowItem deserialize(JsonElement element) {
			JsonObject object = element.getAsJsonObject();
			JsonElement rawCount = object.get("count");
			ChatId id = ChatId.parse(object.get("id").getAsString());
			int count = rawCount != null ? rawCount.getAsInt() : 1;
			return new ShowItem(id, count);
		}
	}
}