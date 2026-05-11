package vakiliner.chatcomponentapi.fabric;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

class HoverEventContents implements IStyleParser {
	private static final Constructor<Style> STYLE_CONSTRUCTOR;
	private static final Field ITEM_FIELD;
	private static final Field COUND_FIELD;

	static {
		try {
			STYLE_CONSTRUCTOR = Style.class.getDeclaredConstructor(TextColor.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, ClickEvent.class, HoverEvent.class, String.class, ResourceLocation.class);
			STYLE_CONSTRUCTOR.setAccessible(true);
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		try {
			ITEM_FIELD = HoverEvent.ItemStackInfo.class.getDeclaredField("field_24355");
			ITEM_FIELD.setAccessible(true);
			COUND_FIELD = HoverEvent.ItemStackInfo.class.getDeclaredField("field_24356");
			COUND_FIELD.setAccessible(true);
		} catch (NoSuchFieldException err) {
			throw new IllegalStateException(err);
		}
	}

	public ChatTextColor injectColor(Style style) {
		return fabric(style.getColor());
	}

	public Style fabric(ChatStyle chatStyle) {
		if (chatStyle.isEmpty()) return Style.EMPTY;
		try {
			return STYLE_CONSTRUCTOR.newInstance(fabric(chatStyle.getColor()), chatStyle.getBold(), chatStyle.getItalic(), chatStyle.getUnderlined(), chatStyle.getStrikethrough(), chatStyle.getObfuscated(), FabricParser.fabric(chatStyle.getClickEvent()), fabric(chatStyle.getHoverEvent()), chatStyle.getInsertion(), FabricParser.fabric(chatStyle.getFont()));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
	}

	public HoverEvent fabric(ChatHoverEvent<?> event) {
		ChatHoverEvent.Action<?> action = event.getAction();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, FabricParser.fabric(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, fabric(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, fabric(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public ChatHoverEvent<?> fabric(HoverEvent event) {
		HoverEvent.Action<?> action = event.getAction();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, FabricParser.fabric(event.getValue(HoverEvent.Action.SHOW_TEXT)));
		} else if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, fabric(event.getValue(HoverEvent.Action.SHOW_ENTITY)));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, fabric(event.getValue(HoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public static HoverEvent.EntityTooltipInfo fabric(ChatHoverEvent.ShowEntity content) {
		return content != null ? new HoverEvent.EntityTooltipInfo(Registry.ENTITY_TYPE.get(FabricParser.fabric(content.getType())), content.getUniqueId(), FabricParser.fabric(content.getName())) : null;
	}

	public static ChatHoverEvent.ShowEntity fabric(HoverEvent.EntityTooltipInfo content) {
		return content != null ? new ChatHoverEvent.ShowEntity(FabricParser.fabric(Registry.ENTITY_TYPE.getKey(content.type)), content.id, FabricParser.fabric(content.name)) : null;
	}

	public static HoverEvent.ItemStackInfo fabric(ChatHoverEvent.ShowItem content) {
		return content != null ? new HoverEvent.ItemStackInfo(new ItemStack(Registry.ITEM.get(FabricParser.fabric(content.getItem())), content.getCount())) : null;
	}

	public static ChatHoverEvent.ShowItem fabric(HoverEvent.ItemStackInfo content) {
		if (content == null) return null;
		final Item item;
		final int count;
		try {
			item = (Item) ITEM_FIELD.get(content);
			count = (Integer) COUND_FIELD.get(content);
		} catch (IllegalAccessException err) {
			throw new IllegalArgumentException(err);
		}
		return new ChatHoverEvent.ShowItem(FabricParser.fabric(Registry.ITEM.getKey(item)), count);
	}

	public static TextColor fabric(ChatTextColor color) {
		return color != null ? TextColor.parseColor(color.toString()) : null;
	}

	public static ChatTextColor fabric(TextColor color) {
		return color != null ? ChatTextColor.of(color.toString()) : null;
	}
}