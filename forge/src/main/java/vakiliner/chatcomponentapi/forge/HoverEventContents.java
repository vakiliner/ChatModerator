package vakiliner.chatcomponentapi.forge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

class HoverEventContents implements IStyleParser {
	private static final Constructor<Style> STYLE_CONSTRUCTOR;
	private static final Field ITEM_FIELD;
	private static final Field COUND_FIELD;

	static {
		try {
			STYLE_CONSTRUCTOR = Style.class.getDeclaredConstructor(Color.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, ClickEvent.class, HoverEvent.class, String.class, ResourceLocation.class);
			STYLE_CONSTRUCTOR.setAccessible(true);
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		try {
			ITEM_FIELD = HoverEvent.ItemHover.class.getDeclaredField("field_240685_a_");
			ITEM_FIELD.setAccessible(true);
			COUND_FIELD = HoverEvent.ItemHover.class.getDeclaredField("field_240686_b_");
			COUND_FIELD.setAccessible(true);
		} catch (NoSuchFieldException err) {
			throw new IllegalStateException(err);
		}
	}

	public ChatTextColor injectColor(Style style) {
		return forge(style.getColor());
	}

	public Style forge(ChatStyle chatStyle) {
		if (chatStyle.isEmpty()) return Style.EMPTY;
		try {
			return STYLE_CONSTRUCTOR.newInstance(forge(chatStyle.getColor()), chatStyle.getBold(), chatStyle.getItalic(), chatStyle.getUnderlined(), chatStyle.getStrikethrough(), chatStyle.getObfuscated(), ForgeParser.forge(chatStyle.getClickEvent()), forge(chatStyle.getHoverEvent()), chatStyle.getInsertion(), ForgeParser.forge(chatStyle.getFont()));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
	}

	public HoverEvent forge(ChatHoverEvent<?> event) {
		ChatHoverEvent.Action<?> action = event.getAction();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, ForgeParser.forge(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, forge(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, forge(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public ChatHoverEvent<?> forge(HoverEvent event) {
		HoverEvent.Action<?> action = event.getAction();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, ForgeParser.forge(event.getValue(HoverEvent.Action.SHOW_TEXT)));
		} else if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, forge(event.getValue(HoverEvent.Action.SHOW_ENTITY)));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, forge(event.getValue(HoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	@SuppressWarnings("deprecation")
	public static HoverEvent.EntityHover forge(ChatHoverEvent.ShowEntity content) {
		return content != null ? new HoverEvent.EntityHover(Registry.ENTITY_TYPE.get(ForgeParser.forge(content.getType())), content.getUniqueId(), ForgeParser.forge(content.getName())) : null;
	}

	@SuppressWarnings("deprecation")
	public static ChatHoverEvent.ShowEntity forge(HoverEvent.EntityHover content) {
		return content != null ? new ChatHoverEvent.ShowEntity(ForgeParser.forge(Registry.ENTITY_TYPE.getKey(content.type)), content.id, ForgeParser.forge(content.name)) : null;
	}

	@SuppressWarnings("deprecation")
	public static HoverEvent.ItemHover forge(ChatHoverEvent.ShowItem content) {
		return content != null ? new HoverEvent.ItemHover(new ItemStack(Registry.ITEM.get(ForgeParser.forge(content.getItem())), content.getCount())) : null;
	}

	@SuppressWarnings("deprecation")
	public static ChatHoverEvent.ShowItem forge(HoverEvent.ItemHover content) {
		if (content == null) return null;
		final Item item;
		final int count;
		try {
			item = (Item) ITEM_FIELD.get(content);
			count = (Integer) COUND_FIELD.get(content);
		} catch (IllegalAccessException err) {
			throw new IllegalArgumentException(err);
		}
		return new ChatHoverEvent.ShowItem(ForgeParser.forge(Registry.ITEM.getKey(item)), count);
	}

	public static Color forge(ChatTextColor color) {
		return color != null ? Color.parseColor(color.toString()) : null;
	}

	public static ChatTextColor forge(Color color) {
		return color != null ? ChatTextColor.of(color.toString()) : null;
	}
}