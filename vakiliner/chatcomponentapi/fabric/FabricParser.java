package vakiliner.chatcomponentapi.fabric;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentFormat;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class FabricParser extends BaseParser {
	public static Component fabric(ChatComponent raw) {
		final BaseComponent component;
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new TextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map(FabricParser::fabric).toArray());
		} else {
			throw new IllegalArgumentException("Could not parse Component from " + raw.getClass());
		}
		component.setStyle(fabricStyle(raw));
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.append(fabric(chatComponent));
		}
		return component;
	}

	public static ChatComponent fabric(Component raw) {
		final ChatComponent chatComponent;
		if (raw != null) {
			return null;
		} else if (raw instanceof TextComponent) {
			TextComponent component = (TextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslatableComponent) {
			TranslatableComponent component = (TranslatableComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getKey(), Arrays.stream(component.getArgs()).map((arg) -> arg instanceof Component ? fabric((Component) arg) : new ChatTextComponent(String.valueOf(arg))).collect(Collectors.toList()));
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		Style style = raw.getStyle();
		chatComponent.setColor(fabricColor(style.getColor()));
		if (style.isBold()) chatComponent.setBold(true);
		if (style.isItalic()) chatComponent.setItalic(true);
		if (style.isStrikethrough()) chatComponent.setStrikethrough(true);
		if (style.isUnderlined()) chatComponent.setUnderlined(true);
		if (style.isObfuscated()) chatComponent.setObfuscated(true);
		chatComponent.setClickEvent(fabric(style.getClickEvent()));
		chatComponent.setHoverEvent(fabric(style.getHoverEvent()));
		chatComponent.setExtra(raw.getSiblings().stream().map(FabricParser::fabric).collect(Collectors.toList()));
		return chatComponent;
	}

	public static ClickEvent fabric(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent fabric(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> HoverEvent fabric(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.getByName(event.getAction().getName()), fabricContent(event.getValue())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<?> fabric(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action<?> action = event.getAction();
		return new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(action.getName()), (V) fabricContent2(event.getValue(action)));
	}

	public static Object fabricContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return fabric(content);
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new HoverEvent.EntityTooltipInfo(Registry.ENTITY_TYPE.get(fabric(content.getType())), content.getUniqueId(), fabric(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new HoverEvent.ItemStackInfo(new ItemStack(Registry.ITEM.get(fabric(content.getItem())), content.getCount()));
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	public static Object fabricContent2(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof Component) {
			Component content = (Component) raw;
			return fabric(content);
		} else if (raw instanceof HoverEvent.EntityTooltipInfo) {
			HoverEvent.EntityTooltipInfo content = (HoverEvent.EntityTooltipInfo) raw;
			return new ChatHoverEvent.ShowEntity(fabric(Registry.ENTITY_TYPE.getKey(content.type)), content.id, fabric(content.name));
		} else if (raw instanceof HoverEvent.ItemStackInfo) {
			HoverEvent.ItemStackInfo content = (HoverEvent.ItemStackInfo) raw;
			ItemStack itemStack = content.getItemStack();
			return new ChatHoverEvent.ShowItem(fabric(Registry.ITEM.getKey(itemStack.getItem())), itemStack.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static ResourceLocation fabric(ChatId id) {
		return id != null ? new ResourceLocation(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId fabric(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static Style fabricStyle(ChatComponent component) {
		Objects.requireNonNull(component);
		Style style = Style.EMPTY;
		TextColor color = fabricColor(component.getColorRaw());
		if (color != null) {
			style = style.withColor(color);
		}
		for (Map.Entry<ChatComponentFormat, Boolean> entry : component.getFormatsRaw().entrySet()) {
			Boolean isSetted = entry.getValue();
			if (isSetted != null && isSetted) {
				style = style.applyFormat(fabric(entry.getKey().asTextFormat()));
			}
		}
		ChatClickEvent clickEvent = component.getClickEvent();
		if (clickEvent != null) style = style.withClickEvent(fabric(clickEvent));
		ChatHoverEvent<?> hoverEvent = component.getHoverEvent();
		if (hoverEvent != null) style = style.withHoverEvent(fabric(hoverEvent));
		return style;
	}

	public static ChatFormatting fabric(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByCode(format.getChar()) : null;
	}

	public static ChatTextFormat fabric(ChatFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByChar(formatting.toString().charAt(1)) : null;
	}

	public static TextColor fabricColor(ChatTextColor color) {
		return color != null ? TextColor.fromRgb(color.value()) : null;
	}

	public static ChatTextColor fabricColor(TextColor color) {
		return color != null ? ChatTextColor.color(color.getValue(), null) : null;
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new FabricChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new FabricChatOfflinePlayer(this, server, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSource commandSource) {
		if (commandSource instanceof ServerPlayer) {
			return this.toChatPlayer((ServerPlayer) commandSource);
		}
		return commandSource != null ? new FabricChatCommandSender(this, commandSource) : null;
	}

	public ChatTeam toChatTeam(PlayerTeam team) {
		return team != null ? new FabricChatTeam(this, team) : null;
	}
}