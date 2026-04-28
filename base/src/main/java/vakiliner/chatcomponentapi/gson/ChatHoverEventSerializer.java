package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent.Action;

public class ChatHoverEventSerializer implements JsonSerializer<ChatHoverEvent<?>>, JsonDeserializer<ChatHoverEvent<?>> {
	public JsonElement serialize(ChatHoverEvent<?> hoverEvent, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("action", hoverEvent.getAction().getName());
		object.add("contents", context.serialize(hoverEvent.getContents()));
		return object;
	}

	public ChatHoverEvent<?> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		final Action<?> action = Action.getByName(object.get("action").getAsString());
		final JsonElement contents;
		if (object.has("contents")) {
			contents = object.get("contents");
		} else if (object.has("value")) {
			if (action == Action.SHOW_TEXT) {
				contents = new Gson().fromJson(object.get("value").getAsString(), JsonElement.class);
			} else {
				contents = new Gson().fromJson(((ChatTextComponent) context.deserialize(new Gson().fromJson(object.get("value").getAsString(), JsonElement.class), ChatComponent.class)).getText(), JsonElement.class);
			}
		} else {
			contents = null;
		}
		if (action == Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(Action.SHOW_TEXT, context.deserialize(contents, ChatComponent.class));
		} else if (action == Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(Action.SHOW_ENTITY, context.deserialize(contents, ChatHoverEvent.ShowEntity.class));
		} else if (action == Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(Action.SHOW_ITEM, context.deserialize(contents, ChatHoverEvent.ShowItem.class));
		} else {
			throw new JsonParseException("Unknown action");
		}
	}

	public static class ShowEntity implements JsonSerializer<ChatHoverEvent.ShowEntity>, JsonDeserializer<ChatHoverEvent.ShowEntity> {
		public JsonElement serialize(ChatHoverEvent.ShowEntity showEntity, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			ChatId type = showEntity.getType();
			UUID id = showEntity.getUniqueId();
			ChatComponent name = showEntity.getName();
			if (id != null) object.addProperty("id", id.toString());
			if (type != null) object.addProperty("type", type.toString());
			if (name != null) object.add("name", context.serialize(name));
			return object;
		}

		public ChatHoverEvent.ShowEntity deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			final ChatId type;
			final UUID id;
			final ChatComponent name;
			if (object.has("type")) {
				type = ChatId.parse(object.get("type").getAsString());
			} else {
				type = null;
			}
			if (object.has("id")) {
				id = UUID.fromString(object.get("id").getAsString());
			} else {
				id = null;
			}
			if (object.has("name")) {
				name = context.deserialize(object.get("name"), ChatComponent.class);
			} else {
				name = null;
			}
			return new ChatHoverEvent.ShowEntity(type, id, name);
		}
	}

	public static class ShowItem implements JsonSerializer<ChatHoverEvent.ShowItem>, JsonDeserializer<ChatHoverEvent.ShowItem> {
		public JsonElement serialize(ChatHoverEvent.ShowItem showItem, Type type, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			object.addProperty("item", showItem.getItem().toString());
			Integer count = showItem.getCount();
			if (count != null) object.addProperty("Count", count);
			return object;
		}

		public ChatHoverEvent.ShowItem deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			final ChatId item = ChatId.parse(object.get("item").getAsString());
			final Integer count;
			if (object.has("Count")) {
				JsonPrimitive Count = object.getAsJsonPrimitive("Count");
				if (Count.isString()) {
					String cString = Count.getAsString();
					char last = cString.charAt(cString.length() - 1);
					if (last == 'b' || last == 's' || last == 'l' || last == 'f' || last == 'd') {
						cString = cString.substring(0, cString.length() - 1);
					}
					count = Integer.parseInt(cString);
				} else {
					count = Count.getAsInt();
				}
			} else {
				count = null;
			}
			return new ChatHoverEvent.ShowItem(item, count);
		}
	}
}