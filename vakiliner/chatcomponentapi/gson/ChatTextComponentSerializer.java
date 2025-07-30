package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

public class ChatTextComponentSerializer extends AbstractChatComponentSerializer<ChatTextComponent> {
	public JsonElement serialize(ChatTextComponent component, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		super.serialize(object, component, context);
		object.addProperty("text", component.getText());
		return object;
	}

	public ChatTextComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		ChatTextComponent component = new ChatTextComponent(object.get("text").getAsString());
		super.deserialize(component, object, context);
		return component;
	}
}