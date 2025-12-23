package vakiliner.chatcomponentapi.spigot;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;
import vakiliner.chatcomponentapi.gson.APIGson;

public class SpigotParser extends BukkitParser {
	private static final HoverEventParser HOVER_EVENT_PARSER;

	static {
		HoverEventParser hoverEventParser;
		try {
			hoverEventParser = new HoverEventParser();
		} catch (NoClassDefFoundError err) {
			hoverEventParser = null;
		}
		HOVER_EVENT_PARSER = hoverEventParser;
	}

	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (sendMessageWithUUID) {
				player.spigot().sendMessage(spigot(type), uuid, spigot(component));
			} else {
				player.spigot().sendMessage(spigot(type), spigot(component));
			}
		} else {
			if (sendMessageWithUUID) {
				sender.spigot().sendMessage(uuid, spigot(component));
			} else {
				sender.spigot().sendMessage(spigot(component));
			}
		}
	}

	public static BaseComponent spigot(ChatComponent raw) {
		final BaseComponent component;
		if (raw instanceof ChatComponentWithLegacyText) {
			raw = ((ChatComponentWithLegacyText) raw).getComponent();
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new TextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map(SpigotParser::spigot).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorComponent(chatComponent.getSelector());
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
		} else if (raw instanceof SelectorComponent) {
			SelectorComponent component = (SelectorComponent) raw;
			chatComponent = new ChatSelectorComponent(component.getSelector());
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

	@SuppressWarnings("deprecation")
	public static HoverEvent spigot(ChatHoverEvent<?> event) {
		if (event == null) return null;
		if (HOVER_EVENT_PARSER != null) return HOVER_EVENT_PARSER.spigot(event);
		return new HoverEvent(HoverEvent.Action.valueOf(event.getAction().getName().toUpperCase()), new BaseComponent[] { spigot(event.getValue()) });
	}

	@SuppressWarnings("deprecation")
	public static <V> ChatHoverEvent<?> spigot(HoverEvent event) {
		if (event == null) return null;
		if (HOVER_EVENT_PARSER != null) return HOVER_EVENT_PARSER.spigot(event);
		HoverEvent.Action action = event.getAction();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, spigot(event.getValue()[0]));
		}
		String json = ((TextComponent) event.getValue()[0]).getText();
		Gson gson = APIGson.builder(true).create();
		switch (action) {
			case SHOW_ENTITY:
				ChatHoverEvent.ShowEntity showEntity = gson.fromJson(json, ChatHoverEvent.ShowEntity.class);
				return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, showEntity);
			case SHOW_ITEM:
				ChatHoverEvent.ShowItem showItem = gson.fromJson(json, ChatHoverEvent.ShowItem.class);
				return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, showItem);
			default: throw new IllegalArgumentException();
		}
	}

	public static net.md_5.bungee.api.ChatMessageType spigot(ChatMessageType type) {
		return type != null ? net.md_5.bungee.api.ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatMessageType spigot(net.md_5.bungee.api.ChatMessageType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatColor spigot(ChatTextFormat color) {
		return color != null ? ChatColor.getByChar(color.getChar()) : null;
	}

	public static ChatTextFormat spigot(ChatColor color) {
		return color != null ? ChatTextFormat.getByName(color.getName()) : null;
	}
}