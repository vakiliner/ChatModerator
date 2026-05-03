package vakiliner.chatcomponentapi.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

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

	@Accessor("font")
	ResourceLocation getFont();
}