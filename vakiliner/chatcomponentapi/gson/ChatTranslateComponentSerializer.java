package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import java.util.Arrays;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class ChatTranslateComponentSerializer extends AbstractChatComponentSerializer<ChatTranslateComponent> {
	public JsonElement serialize(ChatTranslateComponent component, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		super.serialize(object, component, context);
		object.addProperty("translate", component.getKey());
		return object;
	}

	public ChatTranslateComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		ChatTranslateComponent component = new ChatTranslateComponent(null, object.get("translate").getAsString());
		if (object.has("with")) component.setWith(Arrays.asList(context.deserialize(object.get("extra"), ChatComponent[].class)));
		super.deserialize(component, object, context);
		return component;
	}
}