package vakiliner.chatcomponentapi.base;

import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;

public interface ChatTeam {
	String getName();

	ChatComponent getDisplayName();

	ChatNamedColor getColor();

	ChatComponent getPrefix();

	ChatComponent getSuffix();
}