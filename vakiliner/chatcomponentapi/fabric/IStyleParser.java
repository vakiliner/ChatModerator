package vakiliner.chatcomponentapi.fabric;

import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;

interface IStyleParser {
	Style injectStyle(ChatComponent component);

	void copyColor(ChatComponent to, Style from);

	HoverEvent fabric(ChatHoverEvent<?> event);

	ChatHoverEvent<?> fabric(HoverEvent event);
}