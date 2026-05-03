package vakiliner.chatcomponentapi.spigot;

import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

class HoverEventParser {
	public HoverEvent spigot(ChatHoverEvent<?> event) {
		ChatHoverEvent.Action<?> action = event.getAction();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new BaseComponent[] { SpigotParser.spigot(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)) }));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, spigot(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, spigot(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public ChatHoverEvent<?> spigot(HoverEvent event) {
		HoverEvent.Action action = event.getAction();
		Content content = event.getContents().get(0);
		if (action == HoverEvent.Action.SHOW_TEXT) {
			Object value = ((Text) content).getValue();
			final ChatComponent chatComponent;
			if (value instanceof String) {
				chatComponent = new ChatTextComponent((String) value);
			} else if (value instanceof BaseComponent[]) {
				chatComponent = SpigotParser.spigot(((BaseComponent[]) value)[0]);
			} else {
				throw new IllegalArgumentException("Could not parse ChatComponent from " + value.getClass());
			}
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, chatComponent);
		} else if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, spigot((Entity) content));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, spigot((Item) content));
		} else {
			throw new IllegalArgumentException("Unknown action");
		}
	}

	public static Entity spigot(ChatHoverEvent.ShowEntity content) {
		return content != null ? new Entity(content.getType().toString(), content.getUniqueId().toString(), SpigotParser.spigot(content.getName())) : null;
	}

	public static ChatHoverEvent.ShowEntity spigot(Entity content) {
		return content != null ? new ChatHoverEvent.ShowEntity(ChatId.parse(content.getType()), UUID.fromString(content.getId()), SpigotParser.spigot(content.getName())) : null;
	}

	public static Item spigot(ChatHoverEvent.ShowItem content) {
		return content != null ? new Item(content.getItem().toString(), content.getCount(), null) : null;
	}

	public static ChatHoverEvent.ShowItem spigot(Item content) {
		return content != null ? new ChatHoverEvent.ShowItem(ChatId.parse(content.getId()), content.getCount()) : null;
	}
}