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
		return event != null ? new HoverEvent(HoverEvent.Action.valueOf(event.getAction().getName().toUpperCase()), spigotContent(event.getContents())) : null;
	}

	@SuppressWarnings("unchecked")
	public <V> ChatHoverEvent<?> spigot(HoverEvent event) {
		return event != null ? new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(event.getAction().name().toLowerCase()), (V) spigotContent2(event.getContents().get(0))) : null;
	}

	public Content spigotContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return new Text(new BaseComponent[] { SpigotParser.spigot(content) });
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new Entity(content.getType().toString(), content.getUniqueId().toString(), SpigotParser.spigot(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new Item(content.getItem().toString(), content.getCount(), null);
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public Object spigotContent2(Content raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Text) {
			Text content = (Text) raw;
			Object value = content.getValue();
			if (value instanceof String) {
				return new ChatTextComponent((String) value);
			} else if (value instanceof BaseComponent[]) {
				return SpigotParser.spigot(((BaseComponent[]) value)[0]);
			} else {
				throw new IllegalArgumentException("Could not parse ChatTextContent from " + value.getClass());
			}
		} else if (raw instanceof Entity) {
			Entity content = (Entity) raw;
			return new ChatHoverEvent.ShowEntity(new ChatId(content.getType()), UUID.fromString(content.getId()), SpigotParser.spigot(content.getName()));
		} else if (raw instanceof Item) {
			Item content = (Item) raw;
			return new ChatHoverEvent.ShowItem(new ChatId(content.getId()), content.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}
}