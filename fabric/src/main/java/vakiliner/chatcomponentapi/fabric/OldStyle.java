package vakiliner.chatcomponentapi.fabric;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

class OldStyle implements IStyleParser {
	private static final Constructor<Style> STYLE_CONSTRUCTOR;
	private static final Constructor<HoverEvent> HOVER_EVENT_CONSTRUCTOR;
	private static final Field COLOR_FIELD;
	private static final Method HOVER_EVENT_GET_ACTION;
	private static final Method HOVER_EVENT_GET_VALUE;
	private static final Method SET_COLOR;
	private static final Method SET_BOLD;
	private static final Method SET_ITALIC;
	private static final Method SET_UNDERLINED;
	private static final Method SET_STRIKETHROUGH;
	private static final Method SET_OBFUSCATED;
	private static final Method SET_CLICK_EVENT;
	private static final Method SET_HOVER_EVENT;

	static {
		try {
			STYLE_CONSTRUCTOR = Style.class.getConstructor();
			HOVER_EVENT_CONSTRUCTOR = HoverEvent.class.getConstructor(HoverEvent.Action.class, Component.class);
			SET_COLOR = Style.class.getMethod("method_10977", ChatFormatting.class);
			SET_BOLD = Style.class.getMethod("method_10982", Boolean.class);
			SET_ITALIC = Style.class.getMethod("method_10978", Boolean.class);
			SET_UNDERLINED = Style.class.getMethod("method_10968", Boolean.class);
			SET_STRIKETHROUGH = Style.class.getMethod("method_10959", Boolean.class);
			SET_OBFUSCATED = Style.class.getMethod("method_10948", Boolean.class);
			SET_CLICK_EVENT = Style.class.getMethod("method_10958", ClickEvent.class);
			SET_HOVER_EVENT = Style.class.getMethod("method_10949", HoverEvent.class);
			HOVER_EVENT_GET_ACTION = HoverEvent.class.getMethod("method_10892", Object.class);
			HOVER_EVENT_GET_VALUE = HoverEvent.class.getMethod("method_10891", Component.class);
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		try {
			COLOR_FIELD = Style.class.getDeclaredField("field_11855");
			COLOR_FIELD.setAccessible(true);
		} catch (NoSuchFieldException err) {
			throw new IllegalStateException(err);
		}
	}

	public ChatTextColor injectColor(Style style) {
		try {
			return ChatNamedColor.getByFormat(FabricParser.fabric((ChatFormatting) COLOR_FIELD.get(style)));
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		}
	}

	public Style fabric(ChatStyle chatStyle) {
		final Style style;
		try {
			style = STYLE_CONSTRUCTOR.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		if (chatStyle.isEmpty()) return style;
		ChatTextColor color = chatStyle.getColor();
		try {
			SET_COLOR.invoke(style, color != null ? FabricParser.fabric(color.asFormat()) : null);
			SET_BOLD.invoke(style, chatStyle.getBold());
			SET_ITALIC.invoke(style, chatStyle.getItalic());
			SET_UNDERLINED.invoke(style, chatStyle.getUnderlined());
			SET_STRIKETHROUGH.invoke(style, chatStyle.getStrikethrough());
			SET_OBFUSCATED.invoke(style, chatStyle.getObfuscated());
			SET_CLICK_EVENT.invoke(style, FabricParser.fabric(chatStyle.getClickEvent()));
			SET_HOVER_EVENT.invoke(style, FabricParser.fabric(chatStyle.getHoverEvent()));
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		return style;
	}

	@SuppressWarnings("deprecation")
	public HoverEvent fabric(ChatHoverEvent<?> event) {
		try {
			return HOVER_EVENT_CONSTRUCTOR.newInstance(HoverEvent.Action.getByName(event.getAction().getName()), FabricParser.fabric(event.getValue()));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
	}

	public ChatHoverEvent<?> fabric(HoverEvent event) {
		final Object action;
		final Component contents;
		try {
			action = HOVER_EVENT_GET_ACTION.invoke(event);
			contents = (Component) HOVER_EVENT_GET_VALUE.invoke(event);
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, FabricParser.fabric(contents));
		}
		JsonElement value = new Gson().fromJson(((TextComponent) contents).getText(), JsonElement.class);
		if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
		} else {
			throw new IllegalArgumentException();
		}
	}
}