package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class ChatComponentDeserializer implements JsonDeserializer<ChatComponent> {
	public ChatComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		if (element.isJsonPrimitive()) {
			return new ChatTextComponent(element.getAsString());
		}
		JsonObject object = element.getAsJsonObject();
		if (object.has("text")) {
			return context.deserialize(object, ChatTextComponent.class);
		} else if (object.has("translate")) {
			return context.deserialize(object, ChatTranslateComponent.class);
		} else {
			throw new IllegalArgumentException();
		}
	}
}