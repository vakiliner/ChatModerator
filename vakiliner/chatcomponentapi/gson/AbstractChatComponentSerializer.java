package vakiliner.chatcomponentapi.gson;

import java.util.Arrays;
import java.util.List;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;

public abstract class AbstractChatComponentSerializer<Component extends ChatComponent> implements JsonSerializer<Component>, JsonDeserializer<Component> {
	protected void serialize(JsonObject object, Component component, JsonSerializationContext context) {
		ChatTextColor color = component.getColorRaw();
		Boolean bold = component.isBoldRaw();
		Boolean italic = component.isItalicRaw();
		Boolean underlined = component.isUnderlinedRaw();
		Boolean strikethrough = component.isStrikethroughRaw();
		Boolean obfuscated = component.isObfuscatedRaw();
		String insertion = component.getInsertion();
		ChatClickEvent clickEvent = component.getClickEvent();
		ChatHoverEvent<?> hoverEvent = component.getHoverEvent();
		List<ChatComponent> extra = component.getExtra();
		if (color != null) object.addProperty("color", color instanceof ChatNamedColor ? ((ChatNamedColor) color).getName() : '#' + Integer.toString(color.value(), 16));
		if (bold != null) object.addProperty("bold", bold);
		if (italic != null) object.addProperty("italic", italic);
		if (underlined != null) object.addProperty("underlined", underlined);
		if (strikethrough != null) object.addProperty("strikethrough", strikethrough);
		if (obfuscated != null) object.addProperty("obfuscated", obfuscated);
		if (insertion != null) object.addProperty("insertion", insertion);
		if (clickEvent != null) object.add("clickEvent", context.serialize(clickEvent));
		if (hoverEvent != null) object.add("hoverEvent", context.serialize(hoverEvent));
		if (extra != null) object.add("extra", context.serialize(extra));
	}

	public void deserialize(Component component, JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		if (object.has("color")) component.setColor(ChatTextColor.of(object.get("color").getAsString()));
		if (object.has("bold")) component.setBold(object.get("bold").getAsBoolean());
		if (object.has("italic")) component.setItalic(object.get("italic").getAsBoolean());
		if (object.has("underlined")) component.setUnderlined(object.get("underlined").getAsBoolean());
		if (object.has("strikethrough")) component.setStrikethrough(object.get("strikethrough").getAsBoolean());
		if (object.has("obfuscated")) component.setObfuscated(object.get("obfuscated").getAsBoolean());
		if (object.has("insertion")) component.setInsertion(object.get("insertion").getAsString());
		if (object.has("clickEvent")) component.setClickEvent(context.deserialize(object.get("clickEvent"), ChatClickEvent.class));
		if (object.has("hoverEvent")) component.setHoverEvent(context.deserialize(object.get("hoverEvent"), ChatHoverEvent.class));
		if (object.has("extra")) component.setExtra(Arrays.asList(context.deserialize(object.get("extra"), ChatComponent[].class)));
	}
}