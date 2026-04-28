package vakiliner.chatcomponentapi.fabric;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.gson.APIGson;

@SuppressWarnings("unchecked")
class OldStyle implements IStyleParser {
	private static final Class<?> HOVER_EVENT_ACTION;
	private static final Map<ChatHoverEvent.Action<?>, Object> HOVER_EVENT_ACTION_BY_CHAT = new HashMap<>();
	private static final Function<HoverEvent, ?> HOVER_EVENT_GET_ACTION;
	private static final Function<HoverEvent, Component> HOVER_EVENT_GET_VALUE;
	private static final BiFunction<Object, Component, HoverEvent> HOVER_EVENT_CONSTRUCTOR;
	private static final Supplier<Style> STYLE_CONSTRUCTOR;
	private static final Function<Style, ChatFormatting> GET_COLOR;
	private static final BiConsumer<Style, ChatFormatting> SET_COLOR;
	private static final BiConsumer<Style, Boolean> SET_BOLD;
	private static final BiConsumer<Style, Boolean> SET_ITALIC;
	private static final BiConsumer<Style, Boolean> SET_UNDERLINED;
	private static final BiConsumer<Style, Boolean> SET_STRIKETHROUGH;
	private static final BiConsumer<Style, Boolean> SET_OBFUSCATED;
	private static final BiConsumer<Style, ClickEvent> SET_CLICK_EVENT;
	private static final BiConsumer<Style, HoverEvent> SET_HOVER_EVENT;

	static {
		try {
			HOVER_EVENT_ACTION = Class.forName("net.minecraft.class_2568$class_2569");
		} catch (ClassNotFoundException err) {
			throw new RuntimeException(err);
		}
		for (Object raw : HOVER_EVENT_ACTION.getEnumConstants()) {
			Enum<?> action = (Enum<?>) raw;
			HOVER_EVENT_ACTION_BY_CHAT.put(ChatHoverEvent.Action.getByName(action.name().toLowerCase()), action);
		}
		Class<Style> clazz = Style.class;
		try {
			HOVER_EVENT_GET_ACTION = methodGet(HoverEvent.class, "method_10892", Object.class);
			HOVER_EVENT_GET_VALUE = methodGet(HoverEvent.class, "method_10891", Component.class);
			STYLE_CONSTRUCTOR = constructor(clazz);
			HOVER_EVENT_CONSTRUCTOR = constructor(HoverEvent.class, (Class<Object>) HOVER_EVENT_ACTION, Component.class);
			GET_COLOR = methodGet(clazz, "method_10973", ChatFormatting.class);
			SET_COLOR = methodVoid(clazz, "method_10977", ChatFormatting.class);
			SET_BOLD = methodVoid(clazz, "method_10982", Boolean.class);
			SET_ITALIC = methodVoid(clazz, "method_10978", Boolean.class);
			SET_UNDERLINED = methodVoid(clazz, "method_10968", Boolean.class);
			SET_STRIKETHROUGH = methodVoid(clazz, "method_10959", Boolean.class);
			SET_OBFUSCATED = methodVoid(clazz, "method_10948", Boolean.class);
			SET_CLICK_EVENT = methodVoid(clazz, "method_10958", ClickEvent.class);
			SET_HOVER_EVENT = methodVoid(clazz, "method_10949", HoverEvent.class);
		} catch (NoSuchMethodException err) {
			throw new RuntimeException(err);
		}
	}

	private static <T> Supplier<T> constructor(Class<T> clazz) throws NoSuchMethodException {
		Constructor<T> constructor = clazz.getConstructor();
		return () -> {
			try {
				return constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
				throw new RuntimeException(err);
			}
		};
	}

	private static <T, U, R> BiFunction<T, U, R> constructor(Class<R> clazz, Class<T> param1, Class<U> param2) throws NoSuchMethodException {
		Constructor<R> constructor = clazz.getConstructor(param1, param2);
		return (a, b) -> {
			try {
				return constructor.newInstance(a, b);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
				throw new RuntimeException(err);
			}
		};
	}

	private static <T, V> Function<T, V> methodGet(Class<T> clazz, String name, Class<V> returnType) throws NoSuchMethodException {
		Method method = clazz.getMethod(name);
		if (!returnType.isAssignableFrom(method.getReturnType())) throw new ClassCastException();
		return (impl) -> {
			try {
				return (V) method.invoke(impl);
			} catch (IllegalAccessException | InvocationTargetException err) {
				throw new RuntimeException(err);
			}
		};
	}

	private static <T, V> BiConsumer<T, V> methodVoid(Class<T> clazz, String name, Class<V> type) throws NoSuchMethodException {
		Method method = clazz.getMethod(name, type);
		return (impl, value) -> {
			try {
				method.invoke(impl, value);
			} catch (IllegalAccessException | InvocationTargetException err) {
				throw new RuntimeException(err);
			}
		};
	}

	public Style injectStyle(ChatComponent component) {
		Style style = STYLE_CONSTRUCTOR.get();
		ChatTextColor color = component.getColorRaw();
		if (color != null) SET_COLOR.accept(style, FabricParser.fabric(color.asFormat()));
		SET_BOLD.accept(style, component.isBoldRaw());
		SET_ITALIC.accept(style, component.isItalicRaw());
		SET_UNDERLINED.accept(style, component.isUnderlinedRaw());
		SET_STRIKETHROUGH.accept(style, component.isStrikethroughRaw());
		SET_OBFUSCATED.accept(style, component.isObfuscatedRaw());
		SET_CLICK_EVENT.accept(style, FabricParser.fabric(component.getClickEvent()));
		SET_HOVER_EVENT.accept(style, FabricParser.fabric(component.getHoverEvent()));
		return style;
	}

	public void copyColor(ChatComponent to, Style from) {
		to.setColor(ChatNamedColor.getByFormat(FabricParser.fabric(GET_COLOR.apply(from))));
	}

	@SuppressWarnings("deprecation")
	public HoverEvent fabric(ChatHoverEvent<?> event) {
		return event != null ? HOVER_EVENT_CONSTRUCTOR.apply(HOVER_EVENT_ACTION_BY_CHAT.get(event.getAction()), FabricParser.fabric(event.getValue())) : null;
	}

	public ChatHoverEvent<?> fabric(HoverEvent event) {
		if (event == null) return null;
		Object action = HOVER_EVENT_GET_ACTION.apply(event);
		if (action == HOVER_EVENT_ACTION_BY_CHAT.get(ChatHoverEvent.Action.SHOW_TEXT)) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, FabricParser.fabric(HOVER_EVENT_GET_VALUE.apply(event)));
		}
		String json = ((TextComponent) HOVER_EVENT_GET_VALUE.apply(event)).getText();
		Gson gson = APIGson.builder(true).create();
		if (action == HOVER_EVENT_ACTION_BY_CHAT.get(ChatHoverEvent.Action.SHOW_ENTITY)) {
			ChatHoverEvent.ShowEntity showEntity = gson.fromJson(json, ChatHoverEvent.ShowEntity.class);
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, showEntity);
		} else if (action == HOVER_EVENT_ACTION_BY_CHAT.get(ChatHoverEvent.Action.SHOW_ITEM)) {
			ChatHoverEvent.ShowItem showItem = gson.fromJson(json, ChatHoverEvent.ShowItem.class);
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, showItem);
		} else {
			throw new IllegalArgumentException();
		}
	}
}