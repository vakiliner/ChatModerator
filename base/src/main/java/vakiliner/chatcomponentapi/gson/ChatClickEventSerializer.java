package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.component.ChatClickEvent;

public class ChatClickEventSerializer implements JsonSerializer<ChatClickEvent>, JsonDeserializer<ChatClickEvent> {
	public JsonElement serialize(ChatClickEvent clickEvent, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("action", clickEvent.getAction().getName());
		object.addProperty("value", clickEvent.getValue());
		return object;
	}

	public ChatClickEvent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		final ChatClickEvent.Action action = ChatClickEvent.Action.getByName(object.get("action").getAsString());
		final String value;
		if (object.has("value")) {
			value = object.get("value").getAsString();
		} else {
			value = null;
		}
		return new ChatClickEvent(action, value);
	}
}