package vakiliner.chatcomponentapi.fabric;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentFormat;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;

class HoverEventContents implements IStyleParser {
	static {
		TextColor.class.getName();
	}

	public Style injectStyle(ChatComponent component) {
		Style style = Style.EMPTY;
		TextColor color = fabric(component.getColorRaw());
		if (color != null) {
			style = style.withColor(color);
		}
		for (Map.Entry<ChatComponentFormat, Boolean> entry : component.getFormatsRaw().entrySet()) {
			Boolean isSetted = entry.getValue();
			if (isSetted != null && isSetted) {
				style = style.applyFormat(FabricParser.fabric(entry.getKey().asTextFormat()));
			}
		}
		ChatClickEvent clickEvent = component.getClickEvent();
		if (clickEvent != null) style = style.withClickEvent(FabricParser.fabric(clickEvent));
		ChatHoverEvent<?> hoverEvent = component.getHoverEvent();
		if (hoverEvent != null) style = style.withHoverEvent(FabricParser.fabric(hoverEvent));
		return style;
	}

	public void copyColor(ChatComponent component, Style style) {
		component.setColor(fabric(style.getColor()));
	}

	public HoverEvent fabric(ChatHoverEvent<?> event) {
		return hoverEvent(event);
	}

	public ChatHoverEvent<?> fabric(HoverEvent event) {
		return hoverEvent(event);
	}

	@SuppressWarnings("unchecked")
	public static HoverEvent hoverEvent(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.getByName(event.getAction().getName()), fabricContent(event.getContents())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<V> hoverEvent(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action<?> action = event.getAction();
		return new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(action.getName()), (V) fabricContent2(event.getValue(action)));
	}

	public static Object fabricContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return FabricParser.fabric(content);
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new HoverEvent.EntityTooltipInfo(Registry.ENTITY_TYPE.get(FabricParser.fabric(content.getType())), content.getUniqueId(), FabricParser.fabric(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new HoverEvent.ItemStackInfo(new ItemStack(Registry.ITEM.get(FabricParser.fabric(content.getItem())), content.getCount()));
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public static Object fabricContent2(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Component) {
			Component content = (Component) raw;
			return FabricParser.fabric(content);
		} else if (raw instanceof HoverEvent.EntityTooltipInfo) {
			HoverEvent.EntityTooltipInfo content = (HoverEvent.EntityTooltipInfo) raw;
			return new ChatHoverEvent.ShowEntity(FabricParser.fabric(Registry.ENTITY_TYPE.getKey(content.type)), content.id, FabricParser.fabric(content.name));
		} else if (raw instanceof HoverEvent.ItemStackInfo) {
			HoverEvent.ItemStackInfo content = (HoverEvent.ItemStackInfo) raw;
			ItemStack itemStack = content.getItemStack();
			return new ChatHoverEvent.ShowItem(FabricParser.fabric(Registry.ITEM.getKey(itemStack.getItem())), itemStack.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static TextColor fabric(ChatTextColor color) {
		return color != null ? TextColor.parseColor(color.toString()) : null;
	}

	public static ChatTextColor fabric(TextColor color) {
		return color != null ? ChatTextColor.of(color.toString()) : null;
	}
}