package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatTextComponent extends ChatComponent {
	private String text;

	public ChatTextComponent() {
		this("");
	}

	public ChatTextComponent(ChatTextColor color) {
		this("", color);
	}

	public ChatTextComponent(String text) {
		this.text = text;
	}

	public ChatTextComponent(String text, ChatTextColor color) {
		super(color);
		this.text = text;
	}

	public ChatTextComponent(ChatTextComponent component) {
		super(component);
		this.text = component.text;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = Objects.requireNonNull(text);
	}

	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.text;
	}

	public ChatTextComponent clone() {
		return new ChatTextComponent(this);
	}

	public static ChatTextComponent selector(ChatOfflinePlayer player) {
		final ChatTextComponent component;
		ChatTeam team = player.getTeam();
		if (team != null) {
			component = new ChatTextComponent();
			ChatNamedColor color = team.getColor();
			ChatComponent prefix = team.getPrefix();
			ChatComponent suffix = team.getSuffix();
			if (color != ChatNamedColor.RESET) {
				component.color = team.getColor();
			}
			if (prefix != null) {
				component.append(prefix);
			}
			component.append(component);
			if (suffix != null) {
				component.append(suffix);
			}
		} else {
			component = new ChatTextComponent(player.getName());
		}
		component.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getName() + " "));
		component.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, new ChatHoverEvent.ShowEntity(player)));
		return component;
	}

	public static ChatTextComponent team(ChatTeam team) {
		ChatTextComponent component = new ChatTextComponent();
		component.setColor(team.getColor());
		component.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
		component.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTranslateComponent("Message Team","chat.type.team.hover")));
		component.append(new ChatTextComponent("["));
		ChatComponent teamName = team.getDisplayName();
		teamName.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent(team.getName())));
		component.append(teamName);
		component.append(new ChatTextComponent("]"));
		return component;
	}
}