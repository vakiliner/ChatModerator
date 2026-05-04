package vakiliner.chatcomponentapi.base;

import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatTeam {
	String getName();

	ChatNamedColor getColor();

	ChatComponent getDisplayName();

	ChatComponent getPrefix();

	ChatComponent getSuffix();
}