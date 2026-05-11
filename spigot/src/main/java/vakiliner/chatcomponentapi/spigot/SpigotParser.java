package vakiliner.chatcomponentapi.spigot;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitParser;

public class SpigotParser extends BukkitParser {
	private static final HoverEventParser HOVER_EVENT_PARSER;
	private static final boolean supportsFontInStyle;

	static {
		HoverEventParser hoverEventParser;
		try {
			hoverEventParser = new HoverEventParser();
		} catch (Throwable err) {
			hoverEventParser = null;
		}
		HOVER_EVENT_PARSER = hoverEventParser;
		Method method;
		try {
			method = BaseComponent.class.getMethod("setFont");
		} catch (NoSuchMethodException err) {
			method = null;
		}
		supportsFontInStyle = method != null;
	}

	public boolean supportsFontInStyle() {
		return supportsFontInStyle;
	}

	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		this.sendMessage(sender, spigot(component, sender instanceof ConsoleCommandSender), spigot(type), uuid);
	}

	private void sendMessage(CommandSender sender, BaseComponent component, net.md_5.bungee.api.ChatMessageType type, UUID uuid) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (uuid != null && sendMessageWithUUID) {
				player.spigot().sendMessage(type, uuid, component);
			} else {
				player.spigot().sendMessage(type, component);
			}
		} else {
			if (uuid != null && sendMessageWithUUID) {
				sender.spigot().sendMessage(uuid, component);
			} else {
				sender.spigot().sendMessage(component);
			}
		}
	}

	public void broadcast(Iterable<CommandSender> recipients, ChatComponent chatComponent, ChatMessageType chatMessageType, UUID uuid) {
		BaseComponent component = spigot(chatComponent, false);
		BaseComponent consoleComponent = spigot(chatComponent, true);
		net.md_5.bungee.api.ChatMessageType type = spigot(chatMessageType);
		for (CommandSender recipient : recipients) {
			this.sendMessage(recipient, recipient instanceof ConsoleCommandSender ? consoleComponent : component, type, uuid);
		}
	}

	public static BaseComponent spigot(ChatComponent raw) {
		return spigot(raw, false);
	}

	public static BaseComponent spigot(ChatComponent raw, boolean isConsole) {
		final BaseComponent component;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new TextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> spigot(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorComponent(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse BaseComponent from " + raw.getClass());
		}
		ChatStyle chatStyle = raw.getStyle();
		component.setColor(spigot(chatStyle.getColor()));
		component.setBold(chatStyle.getBold());
		component.setItalic(chatStyle.getItalic());
		component.setUnderlined(chatStyle.getUnderlined());
		component.setStrikethrough(chatStyle.getStrikethrough());
		component.setObfuscated(chatStyle.getObfuscated());
		component.setClickEvent(spigot(chatStyle.getClickEvent()));
		component.setHoverEvent(spigot(chatStyle.getHoverEvent()));
		component.setInsertion(chatStyle.getInsertion());
		if (supportsFontInStyle) component.setFont(chatStyle.getFont().toString());
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.addExtra(spigot(chatComponent, isConsole));
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
		chatComponent.setStyle(spigotStyle(raw));
		for (BaseComponent component : raw.getExtra()) {
			chatComponent.append(spigot(component));
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
	public static ChatHoverEvent<?> spigot(HoverEvent event) {
		if (event == null) return null;
		if (HOVER_EVENT_PARSER != null) return HOVER_EVENT_PARSER.spigot(event);
		HoverEvent.Action action = event.getAction();
		BaseComponent contents = event.getValue()[0];
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, spigot(contents));
		}
		JsonElement value = new Gson().fromJson(((TextComponent) contents).getText(), JsonElement.class);
		switch (action) {
			case SHOW_ENTITY: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
			case SHOW_ITEM: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
			default: throw new IllegalArgumentException("Unknown action");
		}
	}

	public static net.md_5.bungee.api.ChatMessageType spigot(ChatMessageType type) {
		return type != null ? net.md_5.bungee.api.ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatMessageType spigot(net.md_5.bungee.api.ChatMessageType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	protected static ChatStyle spigotStyle(BaseComponent component) {
		Objects.requireNonNull(component);
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(spigot(component.getColorRaw()));
		builder.withBold(component.isBoldRaw());
		builder.withItalic(component.isItalicRaw());
		builder.withUnderlined(component.isUnderlinedRaw());
		builder.withStrikethrough(component.isStrikethroughRaw());
		builder.withObfuscated(component.isObfuscatedRaw());
		builder.withClickEvent(spigot(component.getClickEvent()));
		builder.withHoverEvent(spigot(component.getHoverEvent()));
		builder.withInsertion(component.getInsertion());
		if (supportsFontInStyle) builder.withFont(ChatId.of(component.getFont()));
		return builder.build();
	}

	public static ChatColor spigot(ChatTextColor color) {
		return color != null ? ChatColor.of(color.toString()) : null;
	}

	public static ChatTextColor spigot(ChatColor color) {
		if (color == null) return null;
		if (color.getColor() == null) throw new IllegalArgumentException("ChatColor has no color");
		return ChatTextColor.of(color.getName());
	}
}