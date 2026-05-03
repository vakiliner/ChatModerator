package vakiliner.chatcomponentapi.forge;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

interface IStyleParser {
	ChatTextColor injectColor(Style style);

	Style forge(ChatStyle chatStyle);

	HoverEvent forge(ChatHoverEvent<?> event);

	ChatHoverEvent<?> forge(HoverEvent event);
}