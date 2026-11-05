package vakiliner.chatcomponentapi.paper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentFormat;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.spigot.SpigotParser;

public class PaperParser extends SpigotParser {
	@Override
	public boolean supportsFontInStyle() {
		return true;
	}

	@Override
	public void sendMessage(CommandSender sender, ChatComponent chatComponent, ChatMessageType chatMessageType, UUID uuid) {
		this.sendMessage(sender, paper(chatComponent, sender instanceof ConsoleCommandSender), paper(chatMessageType), uuid != null ? Identity.identity(uuid) : null);
	}

	private void sendMessage(Audience audience, Component component, MessageType type, Identity identity) {
		if (identity != null) {
			audience.sendMessage(identity, component, type);
		} else {
			audience.sendMessage(component, type);
		}
	}

	@Override
	public void broadcast(Iterable<CommandSender> recipients, ChatComponent chatComponent, ChatMessageType chatMessageType, UUID uuid) {
		Component component = paper(chatComponent, false);
		Component consoleComponent = paper(chatComponent, true);
		MessageType type = paper(chatMessageType);
		Identity identity = uuid != null ? Identity.identity(uuid) : null;
		for (CommandSender recipient : recipients) {
			this.sendMessage(recipient, recipient instanceof ConsoleCommandSender ? consoleComponent : component, type, identity);
		}
	}

	@Override
	public ChatComponent getDisplayName(Player player) {
		return paper(player.displayName());
	}

	@Override
	public ChatComponent getDisplayName(Team team) {
		return paper(team.displayName());
	}

	@Override
	public ChatComponent getPrefix(Team team) {
		return paper(team.prefix());
	}

	@Override
	public ChatComponent getSuffix(Team team) {
		return paper(team.suffix());
	}

	public static Component paper(ChatComponent raw) {
		return paper(raw, false);
	}

	public static Component paper(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		final ComponentBuilder<?, ?> builder;
		if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			builder = Component.text().content(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			builder = Component.translatable().key(chatComponent.getKey()).args(chatComponent.getWith().stream().map((c) -> paper(c, isConsole)).collect(Collectors.toList()));
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			builder = Component.selector().pattern(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse Component from " + raw.getClass());
		}
		builder.style(paper(raw.getStyle()));
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			builder.append(paper(chatComponent, isConsole));
		}
		return builder.build();
	}

	public static ChatComponent paper(Component raw) {
		if (raw == null) return null;
		final ChatComponent chatComponent;
		if (raw instanceof TextComponent) {
			TextComponent component = (TextComponent) raw;
			chatComponent = new ChatTextComponent(component.content());
		} else if (raw instanceof TranslatableComponent) {
			TranslatableComponent component = (TranslatableComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.key(), component.args().stream().map(PaperParser::paper).collect(Collectors.toList()));
		} else if (raw instanceof SelectorComponent) {
			SelectorComponent component = (SelectorComponent) raw;
			chatComponent = new ChatSelectorComponent(component.pattern());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setStyle(paper(raw.style()));
		for (Component component : raw.children()) {
			chatComponent.append(paper(component));
		}
		return chatComponent;
	}

	public static Style paper(ChatStyle chatStyle) {
		if (chatStyle == null) return null;
		if (chatStyle.isEmpty()) return Style.empty();
		Style.Builder builder = Style.style();
		builder.color(paper(chatStyle.getColor()));
		for (Map.Entry<ChatComponentFormat, Boolean> entry : chatStyle.getFormats().entrySet()) {
			Boolean isSetted = entry.getValue();
			if (isSetted != null) {
				builder.decoration(paper(entry.getKey()), isSetted);
			}
		}
		builder.clickEvent(paper(chatStyle.getClickEvent()));
		builder.hoverEvent(paper(chatStyle.getHoverEvent()));
		builder.insertion(chatStyle.getInsertion());
		builder.font(paper(chatStyle.getFont()));
		return builder.build();
	}

	public static ChatStyle paper(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(paper(style.color()));
		for (Map.Entry<TextDecoration, TextDecoration.State> entry : style.decorations().entrySet()) {
			TextDecoration.State isSetted = entry.getValue();
			if (isSetted != TextDecoration.State.NOT_SET) {
				builder.withFormat(paper(entry.getKey()), isSetted == TextDecoration.State.TRUE);
			}
		}
		builder.withClickEvent(paper(style.clickEvent()));
		builder.withHoverEvent(paper(style.hoverEvent()));
		builder.withInsertion(style.insertion());
		builder.withFont(paper(style.font()));
		return builder.build();
	}

	public static ClickEvent paper(ChatClickEvent event) {
		if (event == null) return null;
		switch (event.action()) {
			case OPEN_URL: return ClickEvent.openUrl(event.value());
			case OPEN_FILE: return ClickEvent.openFile(event.value());
			case RUN_COMMAND: return ClickEvent.runCommand(event.value());
			case SUGGEST_COMMAND: return ClickEvent.suggestCommand(event.value());
			case CHANGE_PAGE: return ClickEvent.changePage(event.value());
			case COPY_TO_CLIPBOARD: return ClickEvent.copyToClipboard(event.value());
			default: throw new IllegalArgumentException("Unknown ChatClickEvent.Action " + event.action());
		}
	}

	public static ChatClickEvent paper(ClickEvent event) {
		if (event == null) return null;
		switch (event.action()) {
			case OPEN_URL: return new ChatClickEvent(ChatClickEvent.Action.OPEN_URL, event.value());
			case OPEN_FILE: return new ChatClickEvent(ChatClickEvent.Action.OPEN_FILE, event.value());
			case RUN_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, event.value());
			case SUGGEST_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, event.value());
			case CHANGE_PAGE: return new ChatClickEvent(ChatClickEvent.Action.CHANGE_PAGE, event.value());
			case COPY_TO_CLIPBOARD: return new ChatClickEvent(ChatClickEvent.Action.COPY_TO_CLIPBOARD, event.value());
			default: throw new IllegalArgumentException("Unknown ClickEvent.Action " + event.action());
		}
	}

	public static HoverEvent<?> paper(ChatHoverEvent<?> event) {
		if (event == null) return null;
		ChatHoverEvent.Action<?> action = event.action();
		if (action == ChatHoverEvent.Action.SHOW_TEXT) {
			return HoverEvent.showText(paper(event.getValue(ChatHoverEvent.Action.SHOW_TEXT)));
		} else if (action == ChatHoverEvent.Action.SHOW_ENTITY) {
			return HoverEvent.showEntity(paper(event.getValue(ChatHoverEvent.Action.SHOW_ENTITY)));
		} else if (action == ChatHoverEvent.Action.SHOW_ITEM) {
			return HoverEvent.showItem(paper(event.getValue(ChatHoverEvent.Action.SHOW_ITEM)));
		} else {
			throw new IllegalArgumentException("Unknown ChatHoverEvent.Action " + action);
		}
	}

	public static ChatHoverEvent<?> paper(HoverEvent<?> event) {
		if (event == null) return null;
		HoverEvent.Action<?> action = event.action();
		Object value = event.value();
		if (action == HoverEvent.Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, paper((Component) value));
		} else if (action == HoverEvent.Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, paper((HoverEvent.ShowEntity) value));
		} else if (action == HoverEvent.Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, paper((HoverEvent.ShowItem) value));
		} else {
			throw new IllegalArgumentException("Unknown HoverEvent.Action " + action);
		}
	}

	public static HoverEvent.ShowEntity paper(ChatHoverEvent.ShowEntity content) {
		return content != null ? HoverEvent.ShowEntity.of(paper(content.getType()), content.getUniqueId(), paper(content.getName())) : null;
	}

	public static ChatHoverEvent.ShowEntity paper(HoverEvent.ShowEntity content) {
		return content != null ? new ChatHoverEvent.ShowEntity(paper(content.type()), content.id(), paper(content.name())) : null;
	}

	public static HoverEvent.ShowItem paper(ChatHoverEvent.ShowItem content) {
		return content != null ? HoverEvent.ShowItem.of(paper(content.getItem()), content.getCount()) : null;
	}

	public static ChatHoverEvent.ShowItem paper(HoverEvent.ShowItem content) {
		return content != null ? new ChatHoverEvent.ShowItem(paper(content.item()), content.count()) : null;
	}

	public static Key paper(ChatId id) {
		return id != null ? Key.key(id.namespace(), id.value()) : null;
	}

	public static ChatId paper(Key key) {
		return key != null ? new ChatId(key.namespace(), key.value()) : null;
	}

	public static MessageType paper(ChatMessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return MessageType.CHAT;
			case SYSTEM: return MessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatMessageType " + type);
		}
	}

	public static ChatMessageType paper(MessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatMessageType.CHAT;
			case SYSTEM: return ChatMessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown MessageType " + type);
		}
	}

	public static NamedTextColor paper(ChatNamedColor color) {
		return color != null ? NamedTextColor.NAMES.value(color.getName()) : null;
	}

	public static ChatNamedColor paper(NamedTextColor color) {
		return color != null ? ChatNamedColor.getByName(color.toString()) : null;
	}

	public static TextDecoration paper(ChatComponentFormat format) {
		return format != null ? TextDecoration.NAMES.value(format.getName()) : null;
	}

	public static ChatComponentFormat paper(TextDecoration decoration) {
		return decoration != null ? ChatComponentFormat.getByName(decoration.toString()) : null;
	}

	public static TextColor paper(ChatTextColor color) {
		if (color == null) {
			return null;
		} else if (color instanceof ChatNamedColor) {
			return paper((ChatNamedColor) color);
		} else {
			return TextColor.color(color.value());
		}
	}

	public static ChatTextColor paper(TextColor color) {
		if (color == null) {
			return null;
		} else if (color instanceof NamedTextColor) {
			return paper((NamedTextColor) color);
		} else {
			return ChatTextColor.color(color.value(), null);
		}
	}

	@Deprecated
	public static TextColor paperColor(ChatTextColor color) {
		return paper(color);
	}

	@Deprecated
	public static ChatTextColor paperColor(TextColor color) {
		return paper(color);
	}
}
