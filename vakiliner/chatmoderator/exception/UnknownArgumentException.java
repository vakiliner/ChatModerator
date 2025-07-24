package vakiliner.chatmoderator.exception;

import java.util.Arrays;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class UnknownArgumentException extends CommandException {
	private final int at;

	public UnknownArgumentException(int at) {
		this(new ChatTranslateComponent("Incorrect argument for command", "command.unknown.argument", ChatNamedColor.RED), at);
	}

	public UnknownArgumentException(ChatComponent component, int at) {
		super(component);
		this.at = at;
	}

	public int at() {
		return this.at;
	}

	public ChatTextComponent getCommandComponent(String fullCommand) {
		String[] args = fullCommand.split(" ");
		String valid = String.join(" ", Arrays.copyOfRange(args, 0, at)) + ' ';
		if (valid.length() > 10) valid = "..." + valid.substring(valid.length() - 10);
		ChatTextComponent component = new ChatTextComponent(ChatNamedColor.RED);
		component.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, fullCommand));
		component.append(new ChatTextComponent(valid, ChatNamedColor.GRAY));
		ChatTextComponent invalid = new ChatTextComponent(String.join(" ", Arrays.copyOfRange(args, at, args.length)));
		invalid.setUnderlined(true);
		component.append(invalid);
		ChatTranslateComponent here = new ChatTranslateComponent("<--[HERE]", "command.context.here");
		here.setItalic(true);
		component.append(here);
		return component;
	}
}