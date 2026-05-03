package vakiliner.chatcomponentapi.fabric;

import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

interface IStyleParser {
	ChatTextColor injectColor(Style style);

	Style fabric(ChatStyle chatStyle);

	HoverEvent fabric(ChatHoverEvent<?> event);

	ChatHoverEvent<?> fabric(HoverEvent event);
}