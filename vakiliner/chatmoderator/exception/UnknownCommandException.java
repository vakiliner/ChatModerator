package vakiliner.chatmoderator.exception;

import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class UnknownCommandException extends CommandException {
	public UnknownCommandException() {
		super(new ChatTranslateComponent("Unknown or incomplete command, see below for error", "command.unknown.command", ChatNamedColor.RED));
	}
}