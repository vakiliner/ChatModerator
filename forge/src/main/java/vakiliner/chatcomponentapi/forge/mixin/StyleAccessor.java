package vakiliner.chatcomponentapi.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

@Mixin(Style.class)
public interface StyleAccessor {
	@Accessor("bold")
	Boolean getBold();

	@Accessor("italic")
	Boolean getItalic();

	@Accessor("underlined")
	Boolean getUnderlined();

	@Accessor("strikethrough")
	Boolean getStrikethrough();

	@Accessor("obfuscated")
	Boolean getObfuscated();

	@Accessor("clickEvent")
	ClickEvent getClickEvent();

	@Accessor("hoverEvent")
	HoverEvent getHoverEvent();

	@Accessor("insertion")
	String getInsertion();
}