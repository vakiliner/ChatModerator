package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import java.util.UUID;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent.Action;

public class ChatHoverEventSerializerWithValue extends ChatHoverEventSerializer {
	@SuppressWarnings("deprecation")
	public JsonElement serialize(ChatHoverEvent<?> hoverEvent, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		Action<?> action = hoverEvent.getAction();
		object.addProperty("action", action.getName());
		object.addProperty("value", context.serialize(hoverEvent.getValue()).toString());
		return object;
	}

	public static class ShowEntity extends ChatHoverEventSerializer.ShowEntity {
		public JsonElement serialize(ChatHoverEvent.ShowEntity showEntity, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			ChatId type = showEntity.getType();
			UUID id = showEntity.getUniqueId();
			ChatComponent name = showEntity.getName();
			if (id != null) object.addProperty("id", id.toString());
			if (type != null) object.addProperty("type", type.toString());
			if (name != null) object.addProperty("name", context.serialize(name).toString());
			return object;
		}
	}
}