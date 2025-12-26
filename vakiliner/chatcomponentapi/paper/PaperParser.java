package vakiliner.chatcomponentapi.paper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
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
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentFormat;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.spigot.SpigotParser;

public class PaperParser extends SpigotParser {
	public void sendMessage(CommandSender sender, ChatComponent component, ChatMessageType type, UUID uuid) {
		boolean isConsole = sender instanceof ConsoleCommandSender;
		if (uuid != null && sendMessageWithUUID) {
			sender.sendMessage(Identity.identity(uuid), paper(component, isConsole), paper(type));
		} else {
			sender.sendMessage(paper(component, isConsole), paper(type));
		}
	}

	public static Component paper(ChatComponent raw) {
		return paper(raw, false);
	}

	public static Component paper(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		if (raw instanceof ChatComponentModified) {
			if (isConsole && raw instanceof ChatComponentWithLegacyText) {
				raw = ((ChatComponentWithLegacyText) raw).getLegacyComponent();
			} else {
				raw = ((ChatComponentModified) raw).getComponent();
			}
		}
		final ComponentBuilder<?, ?> builder;
		Style style = paperStyle(raw);
		List<Component> children = new ArrayList<>();
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			children.add(paper(chatComponent));
		}
		if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			builder = Component.text().content(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			builder = Component.translatable().key(chatComponent.getKey()).args(chatComponent.getWith().stream().map(PaperParser::paper).collect(Collectors.toList()));
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			builder = Component.selector().pattern(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse Component from " + raw.getClass());
		}
		return builder.style(style).append(children).build();
	}

	public static ChatComponent paper(Component raw) {
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof TextComponent) {
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
		Style style = raw.style();
		chatComponent.setColor(paper(style.color()));
		for (Map.Entry<TextDecoration, TextDecoration.State> entry : raw.decorations().entrySet()) {
			TextDecoration.State isSetted = entry.getValue();
			if (isSetted != TextDecoration.State.NOT_SET) {
				chatComponent.setFormat(paper(entry.getKey()), isSetted == TextDecoration.State.TRUE);
			}
		}
		chatComponent.setClickEvent(paper(raw.clickEvent()));
		chatComponent.setHoverEvent(paper(raw.hoverEvent()));
		List<Component> children = raw.children();
		if (children != null) {
			chatComponent.setExtra(children.stream().map(PaperParser::paper).collect(Collectors.toList()));
		}
		return chatComponent;
	}

	public static ClickEvent paper(ChatClickEvent event) {
		return event != null ? ClickEvent.clickEvent(ClickEvent.Action.NAMES.value(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent paper(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.action().toString()), event.value()) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> HoverEvent<V> paper(ChatHoverEvent<?> event) {
		return event != null ? HoverEvent.hoverEvent((HoverEvent.Action<V>) HoverEvent.Action.NAMES.value(event.getAction().getName()), (V) paperContent(event.getContents())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<V> paper(HoverEvent<?> event) {
		return event != null ? new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(event.action().toString()), (V) paperContent2(event.value())) : null;
	}

	public static Object paperContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return paper(content);
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return HoverEvent.ShowEntity.of(paper(content.getType()), content.getUniqueId(), paper(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return HoverEvent.ShowItem.of(paper(content.getItem()), content.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public static Object paperContent2(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Component) {
			Component content = (Component) raw;
			return paper(content);
		} else if (raw instanceof HoverEvent.ShowEntity) {
			HoverEvent.ShowEntity content = (HoverEvent.ShowEntity) raw;
			return new ChatHoverEvent.ShowEntity(paper(content.type()), content.id(), paper(content.name()));
		} else if (raw instanceof HoverEvent.ShowItem) {
			HoverEvent.ShowItem content = (HoverEvent.ShowItem) raw;
			return new ChatHoverEvent.ShowItem(paper(content.item()), content.count());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static Key paper(ChatId id) {
		return id != null ? Key.key(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId paper(Key key) {
		return key != null ? new ChatId(key.namespace(), key.value()) : null;
	}

	public static MessageType paper(ChatMessageType type) {
		return type != null ? MessageType.valueOf(type.name()) : null;
	}

	public static ChatMessageType paper(MessageType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static Style paperStyle(ChatComponent component) {
		Objects.requireNonNull(component);
		Style.Builder builder = Style.style();
		ChatTextColor color = component.getColor();
		if (color != null) {
			builder.color(paper(color));
		}
		for (Map.Entry<ChatComponentFormat, Boolean> entry : component.getFormatsRaw().entrySet()) {
			Boolean isSetted = entry.getValue();
			if (isSetted != null) {
				builder.decoration(paper(entry.getKey()), isSetted);
			}
		}
		builder.clickEvent(paper(component.getClickEvent()));
		builder.hoverEvent(paper(component.getHoverEvent()));
		return builder.build();
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

	public ChatPlayer toChatPlayer(Player player) {
		return player != null ? new PaperChatPlayer(this, player) : null;
	}

	public ChatTeam toChatTeam(Team team) {
		return team != null ? new PaperChatTeam(this, team) : null;
	}
}