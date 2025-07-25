package vakiliner.chatmoderator.bukkit.exception;

import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public abstract class CommandException extends Exception {
	private final ChatComponent error;

	public CommandException(ChatComponent error) {
		this.error = error;
	}

	public ChatComponent getErrorComponent() {
		return this.error;
	}

	public ChatTextComponent getCommandComponent(final String fullCommand) {
		String valid = fullCommand.length() > 10 ? "..." + fullCommand.substring(fullCommand.length() - 10) : fullCommand;
		ChatTextComponent component = new ChatTextComponent(ChatNamedColor.RED);
		component.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, fullCommand));
		component.append(new ChatTextComponent(valid, ChatNamedColor.GRAY));
		ChatTranslateComponent here = new ChatTranslateComponent("<--[HERE]", "command.context.here");
		here.setItalic(true);
		component.append(here);
		return component;
	}
}