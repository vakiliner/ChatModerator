package vakiliner.chatcomponentapi.spigot;

import static java.util.Objects.requireNonNull;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
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
			method = BaseComponent.class.getMethod("setFont", String.class);
		} catch (NoSuchMethodException err) {
			method = null;
		}
		supportsFontInStyle = method != null;
	}

	@Override
	public boolean supportsFontInStyle() {
		return supportsFontInStyle;
	}

	@Override
	public void sendMessage(CommandSender sender, ChatComponent chatComponent, vakiliner.chatcomponentapi.common.ChatMessageType chatMessageType, UUID uuid) {
		this.sendMessage(sender, spigot(chatComponent, sender instanceof ConsoleCommandSender), spigot(chatMessageType), uuid);
	}

	private void sendMessage(CommandSender sender, BaseComponent component, ChatMessageType type, UUID uuid) {
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

	@Override
	public void broadcast(Iterable<CommandSender> recipients, ChatComponent chatComponent, vakiliner.chatcomponentapi.common.ChatMessageType chatMessageType, UUID uuid) {
		BaseComponent component = spigot(chatComponent, false);
		BaseComponent consoleComponent = spigot(chatComponent, true);
		ChatMessageType type = spigot(chatMessageType);
		for (CommandSender recipient : recipients) {
			this.sendMessage(recipient, recipient instanceof ConsoleCommandSender ? consoleComponent : component, type, uuid);
		}
	}

	public static BaseComponent spigot(ChatComponent raw) {
		return spigot(raw, false);
	}

	public static BaseComponent spigot(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		final BaseComponent component;
		if (raw instanceof ChatTextComponent) {
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
		if (supportsFontInStyle) {
			ChatId font = chatStyle.getFont();
			component.setFont(font != null ? font.toString() : null);
		}
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.addExtra(spigot(chatComponent, isConsole));
		}
		return component;
	}

	public static ChatComponent spigot(BaseComponent raw) {
		if (raw == null) return null;
		final ChatComponent chatComponent;
		if (raw instanceof TextComponent) {
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

	protected static ChatComponent spigot(BaseComponent[] components) {
		return spigot(Arrays.asList(components));
	}

	protected static ChatComponent spigot(List<BaseComponent> components) {
		if (components.size() == 1) {
			return spigot(components.get(0));
		}
		ChatTextComponent chatComponent = new ChatTextComponent();
		components.stream().map(SpigotParser::spigot).forEach(chatComponent::append);
		return chatComponent;
	}

	public static ClickEvent spigot(ChatClickEvent event) {
		if (event == null) return null;
		switch (event.action()) {
			case OPEN_URL: return new ClickEvent(ClickEvent.Action.OPEN_URL, event.value());
			case OPEN_FILE: return new ClickEvent(ClickEvent.Action.OPEN_FILE, event.value());
			case RUN_COMMAND: return new ClickEvent(ClickEvent.Action.RUN_COMMAND, event.value());
			case SUGGEST_COMMAND: return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, event.value());
			case CHANGE_PAGE: return new ClickEvent(ClickEvent.Action.CHANGE_PAGE, event.value());
			case COPY_TO_CLIPBOARD: return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, event.value());
			default: throw new IllegalArgumentException("Unknown ChatClickEvent.Action " + event.action());
		}
	}

	public static ChatClickEvent spigot(ClickEvent event) {
		if (event == null) return null;
		switch (event.getAction()) {
			case OPEN_URL: return new ChatClickEvent(ChatClickEvent.Action.OPEN_URL, event.getValue());
			case OPEN_FILE: return new ChatClickEvent(ChatClickEvent.Action.OPEN_FILE, event.getValue());
			case RUN_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, event.getValue());
			case SUGGEST_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, event.getValue());
			case CHANGE_PAGE: return new ChatClickEvent(ChatClickEvent.Action.CHANGE_PAGE, event.getValue());
			case COPY_TO_CLIPBOARD: return new ChatClickEvent(ChatClickEvent.Action.COPY_TO_CLIPBOARD, event.getValue());
			default: throw new IllegalArgumentException("Unknown ClickEvent.Action " + event.getAction());
		}
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
		BaseComponent[] contents = event.getValue();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, spigot(contents));
		}
		JsonElement value = new Gson().fromJson(((TextComponent) contents[0]).getText(), JsonElement.class);
		switch (action) {
			case SHOW_ENTITY: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, ChatHoverEvent.ShowEntity.deserialize(value, true));
			case SHOW_ITEM: return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, ChatHoverEvent.ShowItem.deserialize(value));
			default: throw new IllegalArgumentException("Unknown HoverEvent.Action " + action);
		}
	}

	public static ChatMessageType spigot(vakiliner.chatcomponentapi.common.ChatMessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatMessageType.CHAT;
			case SYSTEM: return ChatMessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown vakiliner.chatcomponentapi.common.ChatMessageType " + type);
		}
	}

	public static vakiliner.chatcomponentapi.common.ChatMessageType spigot(ChatMessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return vakiliner.chatcomponentapi.common.ChatMessageType.CHAT;
			case SYSTEM: return vakiliner.chatcomponentapi.common.ChatMessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown net.md_5.bungee.api.ChatMessageType " + type);
		}
	}

	protected static ChatStyle spigotStyle(BaseComponent component) {
		requireNonNull(component);
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(spigotColor(component.getColorRaw()));
		builder.withBold(component.isBoldRaw());
		builder.withItalic(component.isItalicRaw());
		builder.withUnderlined(component.isUnderlinedRaw());
		builder.withStrikethrough(component.isStrikethroughRaw());
		builder.withObfuscated(component.isObfuscatedRaw());
		builder.withClickEvent(spigot(component.getClickEvent()));
		builder.withHoverEvent(spigot(component.getHoverEvent()));
		builder.withInsertion(component.getInsertion());
		if (supportsFontInStyle) builder.withFont(ChatId.fromString(component.getFont()));
		return builder.build();
	}

	public static ChatColor spigot(ChatTextFormat format) {
		return format != null ? ChatColor.getByChar(format.getChar()) : null;
	}

	@SuppressWarnings("deprecation")
	public static ChatTextFormat spigot(ChatColor color) {
		if (color == null) return null;
		// Check if ChatColor is a custom color
		color.ordinal();
		return ChatTextFormat.getByName(color.getName());
	}

	public static ChatColor spigot(ChatTextColor color) {
		return color != null ? ChatColor.class.isEnum() ? spigot(color.asFormat()) : ChatColor.of(color.toString()) : null;
	}

	public static ChatTextColor spigotColor(ChatColor color) {
		if (color == null) return null;
		if (ChatColor.class.isEnum() || color.getColor() == null) throw new IllegalArgumentException("ChatColor has no color");
		return ChatTextColor.of(color.getName());
	}
}
