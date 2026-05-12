package vakiliner.chatcomponentapi.forge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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
	private static final Method SET_INSERTION;

	static {
		try {
			STYLE_CONSTRUCTOR = Style.class.getConstructor();
			HOVER_EVENT_CONSTRUCTOR = HoverEvent.class.getConstructor(HoverEvent.Action.class, ITextComponent.class);
			SET_COLOR = Style.class.getMethod("func_150238_a", TextFormatting.class);
			SET_BOLD = Style.class.getMethod("func_150227_a", Boolean.class);
			SET_ITALIC = Style.class.getMethod("func_150217_b", Boolean.class);
			SET_UNDERLINED = Style.class.getMethod("func_150228_d", Boolean.class);
			SET_STRIKETHROUGH = Style.class.getMethod("func_150225_c", Boolean.class);
			SET_OBFUSCATED = Style.class.getMethod("func_150237_e", Boolean.class);
			SET_CLICK_EVENT = Style.class.getMethod("func_150241_a", ClickEvent.class);
			SET_HOVER_EVENT = Style.class.getMethod("func_150209_a", HoverEvent.class);
			SET_INSERTION = Style.class.getMethod("func_179989_a", HoverEvent.class);
			HOVER_EVENT_GET_ACTION = HoverEvent.class.getMethod("func_150701_a");
			HOVER_EVENT_GET_VALUE = HoverEvent.class.getMethod("func_150702_b");
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		try {
			COLOR_FIELD = Style.class.getDeclaredField("field_150247_b");
			COLOR_FIELD.setAccessible(true);
		} catch (NoSuchFieldException err) {
			throw new IllegalStateException(err);
		}
	}

	public ChatTextColor injectColor(Style style) {
		try {
			return ChatNamedColor.getByFormat(ForgeParser.forge((TextFormatting) COLOR_FIELD.get(style)));
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		}
	}

	public Style forge(ChatStyle chatStyle) {
		final Style style;
		try {
			style = STYLE_CONSTRUCTOR.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		if (chatStyle.isEmpty()) return style;
		ChatTextColor color = chatStyle.getColor();
		try {
			SET_COLOR.invoke(style, color != null ? ForgeParser.forge(color.asFormat()) : null);
			SET_BOLD.invoke(style, chatStyle.getBold());
			SET_ITALIC.invoke(style, chatStyle.getItalic());
			SET_UNDERLINED.invoke(style, chatStyle.getUnderlined());
			SET_STRIKETHROUGH.invoke(style, chatStyle.getStrikethrough());
			SET_OBFUSCATED.invoke(style, chatStyle.getObfuscated());
			SET_CLICK_EVENT.invoke(style, ForgeParser.forge(chatStyle.getClickEvent()));
			SET_HOVER_EVENT.invoke(style, ForgeParser.forge(chatStyle.getHoverEvent()));
			SET_INSERTION.invoke(style, chatStyle.getInsertion());
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		return style;
	}

	@SuppressWarnings("deprecation")
	public HoverEvent forge(ChatHoverEvent<?> event) {
		try {
			return HOVER_EVENT_CONSTRUCTOR.newInstance(HoverEvent.Action.getByName(event.getAction().getName()), ForgeParser.forge(event.getValue()));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
	}

	@SuppressWarnings("rawtypes")
	public ChatHoverEvent<?> forge(HoverEvent event) {
		final HoverEvent.Action action;
		final ITextComponent contents;
		try {
			action = (HoverEvent.Action) HOVER_EVENT_GET_ACTION.invoke(event);
			contents = (ITextComponent) HOVER_EVENT_GET_VALUE.invoke(event);
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, ForgeParser.forge(contents));
		}
		JsonElement value = new Gson().fromJson(((StringTextComponent) contents).getText(), JsonElement.class);
		if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
		} else {
			throw new IllegalArgumentException();
		}
	}
}