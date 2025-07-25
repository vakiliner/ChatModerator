package vakiliner.chatcomponentapi.spigot;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;

public class SpigotParser extends BukkitParser {
	public void sendMessage(CommandSender sender, ChatComponent component) {
		sender.spigot().sendMessage(spigot(component));
	}

	public void sendMessage(CommandSender sender, UUID uuid, ChatComponent component) {
		sender.spigot().sendMessage(uuid, spigot(component));
	}

	public static BaseComponent spigot(ChatComponent raw) {
		final BaseComponent component;
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new TextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map(SpigotParser::spigot).toArray());
		} else {
			throw new IllegalArgumentException("Could not parse BaseComponent from " + raw.getClass());
		}
		ChatTextColor color = raw.getColorRaw();
		if (color != null) component.setColor(spigot(color.asFormat()));
		component.setBold(raw.isBoldRaw());
		component.setItalic(raw.isItalicRaw());
		component.setUnderlined(raw.isUnderlinedRaw());
		component.setStrikethrough(raw.isStrikethroughRaw());
		component.setObfuscated(raw.isObfuscatedRaw());
		component.setClickEvent(spigot(raw.getClickEvent()));
		component.setHoverEvent(spigot(raw.getHoverEvent()));
		List<ChatComponent> children = raw.getExtra();
		if (children != null) {
			component.setExtra(children.stream().map(SpigotParser::spigot).collect(Collectors.toList()));
		}
		return component;
	}

	public static ChatComponent spigot(BaseComponent raw) {
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof TextComponent) {
			TextComponent component = (TextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslatableComponent) {
			TranslatableComponent component = (TranslatableComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getTranslate(), component.getWith().stream().map(SpigotParser::spigot).collect(Collectors.toList()));
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setColor(ChatNamedColor.getByFormat(spigot(raw.getColorRaw())));
		chatComponent.setBold(raw.isBoldRaw());
		chatComponent.setItalic(raw.isItalicRaw());
		chatComponent.setUnderlined(raw.isUnderlinedRaw());
		chatComponent.setStrikethrough(raw.isStrikethroughRaw());
		chatComponent.setObfuscated(raw.isObfuscatedRaw());
		chatComponent.setClickEvent(spigot(raw.getClickEvent()));
		chatComponent.setHoverEvent(spigot(raw.getHoverEvent()));
		List<BaseComponent> extra = raw.getExtra();
		if (extra != null) {
			chatComponent.setExtra(extra.stream().map(SpigotParser::spigot).collect(Collectors.toList()));
		}
		return chatComponent;
	}

	public static ClickEvent spigot(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.valueOf(event.getAction().name()), event.getValue()) : null;
	}

	public static ChatClickEvent spigot(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().name().toLowerCase()), event.getValue()) : null;
	}

	public static HoverEvent spigot(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.valueOf(event.getAction().getName().toUpperCase()), spigotContent(event.getValue())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<?> spigot(HoverEvent event) {
		return event != null ? new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(event.getAction().name().toLowerCase()), (V) spigotContent2(event.getContents().get(0))) : null;
	}

	public static Content spigotContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return new Text(new BaseComponent[] { spigot(content) });
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new Entity(content.getType().toString(), content.getUniqueId().toString(), spigot(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new Item(content.getItem().toString(), content.getCount(), null);
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public static Object spigotContent2(Content raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Text) {
			Text content = (Text) raw;
			Object value = content.getValue();
			if (value instanceof String) {
				return new ChatTextComponent((String) value);
			} else if (value instanceof BaseComponent[]) {
				return spigot(((BaseComponent[]) value)[0]);
			} else {
				throw new IllegalArgumentException("Could not parse ChatTextContent from " + value.getClass());
			}
		} else if (raw instanceof Entity) {
			Entity content = (Entity) raw;
			return new ChatHoverEvent.ShowEntity(new ChatId(content.getType()), UUID.fromString(content.getId()), spigot(content.getName()));
		} else if (raw instanceof Item) {
			Item content = (Item) raw;
			return new ChatHoverEvent.ShowItem(new ChatId(content.getId()), content.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static ChatColor spigot(ChatTextFormat color) {
		return color != null ? ChatColor.getByChar(color.getChar()) : null;
	}

	public static ChatTextFormat spigot(ChatColor color) {
		return color != null ? ChatTextFormat.getByChar(color.toString().charAt(1)) : null;
	}
}