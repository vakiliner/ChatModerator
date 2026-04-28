package vakiliner.chatcomponentapi.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public final class APIGson {
	public static final ChatComponentDeserializer COMPONENT = new ChatComponentDeserializer();
	public static final ChatTextComponentSerializer TEXT_COMPONENT = new ChatTextComponentSerializer();
	public static final ChatTranslateComponentSerializer TRANSLATE_COMPONENT = new ChatTranslateComponentSerializer();
	public static final ChatClickEventSerializer CLICK_EVENT = new ChatClickEventSerializer();
	public static final ChatHoverEventSerializer HOVER_EVENT = new ChatHoverEventSerializer();
	public static final ChatHoverEventSerializerWithValue HOVER_EVENT_WITH_VALUE = new ChatHoverEventSerializerWithValue();
	public static final ChatHoverEventSerializer.ShowEntity SHOW_ENTITY = new ChatHoverEventSerializer.ShowEntity();
	public static final ChatHoverEventSerializerWithValue.ShowEntity SHOW_ENTITY_WITH_VALUE = new ChatHoverEventSerializerWithValue.ShowEntity();
	public static final ChatHoverEventSerializer.ShowItem SHOW_ITEM = new ChatHoverEventSerializer.ShowItem();

	private APIGson() {
		throw new UnsupportedOperationException();
	}

	public static GsonBuilder builder(boolean hoverEventWithValue) {
		return new GsonBuilder().registerTypeAdapter(ChatComponent.class, COMPONENT).registerTypeAdapter(ChatTextComponent.class, TEXT_COMPONENT).registerTypeAdapter(ChatTranslateComponent.class, TRANSLATE_COMPONENT).registerTypeAdapter(ChatClickEvent.class, CLICK_EVENT).registerTypeAdapter(ChatHoverEvent.class, !hoverEventWithValue ? HOVER_EVENT : HOVER_EVENT_WITH_VALUE).registerTypeAdapter(ChatHoverEvent.ShowEntity.class, !hoverEventWithValue ? SHOW_ENTITY : SHOW_ENTITY_WITH_VALUE).registerTypeAdapter(ChatHoverEvent.ShowItem.class, SHOW_ITEM);
	}

	public static Gson create() {
		return builder(false).create();
	}
}