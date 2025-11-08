package vakiliner.chatcomponentapi.forge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentFormat;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.forge.mixin.StyleMixin;

public class ForgeParser extends BaseParser {
	public void sendMessage(ICommandSource commandSource, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (commandSource instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) commandSource;
			player.sendMessage(forge(component), forge(type), uuid != null ? uuid : Util.NIL_UUID);
		} else {
			commandSource.sendMessage(forge(component), uuid != null ? uuid : Util.NIL_UUID);
		}
	}

	public static ITextComponent forge(ChatComponent raw) {
		final TextComponent component;
		if (raw instanceof ChatComponentWithLegacyText) {
			raw = ((ChatComponentWithLegacyText) raw).getComponent();
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new StringTextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslationTextComponent(chatComponent.getKey(), chatComponent.getWith().stream().map(ForgeParser::forge).toArray());
		} else {
			throw new IllegalArgumentException("Could not parse ITextComponent from " + raw.getClass());
		}
		component.setStyle(forgeStyle(raw));
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.append(forge(chatComponent));
		}
		return component;
	}

	public static ChatComponent forge(ITextComponent raw) {
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof StringTextComponent) {
			StringTextComponent component = (StringTextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslationTextComponent) {
			TranslationTextComponent component = (TranslationTextComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getKey(), Arrays.stream(component.getArgs()).map((arg) -> arg instanceof ITextComponent ? forge((ITextComponent) arg) : new ChatTextComponent(String.valueOf(arg))).collect(Collectors.toList()));
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		Style style = raw.getStyle();
		chatComponent.setColor(forgeColor(style.getColor()));
		chatComponent.setBold(((StyleMixin) style).getBold());
		chatComponent.setItalic(((StyleMixin) style).getItalic());
		chatComponent.setStrikethrough(((StyleMixin) style).getStrikethrough());
		chatComponent.setUnderlined(((StyleMixin) style).getUnderlined());
		chatComponent.setObfuscated(((StyleMixin) style).getObfuscated());
		chatComponent.setClickEvent(forge(style.getClickEvent()));
		chatComponent.setHoverEvent(forge(style.getHoverEvent()));
		chatComponent.setExtra(raw.getSiblings().stream().map(ForgeParser::forge).collect(Collectors.toList()));
		return chatComponent;
	}

	public static ClickEvent forge(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent forge(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	@SuppressWarnings({ "unchecked", "null" })
	public static <V> HoverEvent forge(ChatHoverEvent<?> event) {
		return event != null ? new HoverEvent(HoverEvent.Action.getByName(event.getAction().getName()), forgeContent(event.getContents())) : null;
	}

	@SuppressWarnings("unchecked")
	public static <V> ChatHoverEvent<?> forge(HoverEvent event) {
		if (event == null) return null;
		HoverEvent.Action<?> action = event.getAction();
		return new ChatHoverEvent<>((ChatHoverEvent.Action<V>) ChatHoverEvent.Action.getByName(action.getName()), (V) forgeContent2(event.getValue(action)));
	}

	@SuppressWarnings("deprecation")
	public static Object forgeContent(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatComponent) {
			ChatComponent content = (ChatComponent) raw;
			return forge(content);
		} else if (raw instanceof ChatHoverEvent.ShowEntity) {
			ChatHoverEvent.ShowEntity content = (ChatHoverEvent.ShowEntity) raw;
			return new HoverEvent.EntityHover(Registry.ENTITY_TYPE.get(forge(content.getType())), content.getUniqueId(), forge(content.getName()));
		} else if (raw instanceof ChatHoverEvent.ShowItem) {
			ChatHoverEvent.ShowItem content = (ChatHoverEvent.ShowItem) raw;
			return new HoverEvent.ItemHover(new ItemStack(Registry.ITEM.get(forge(content.getItem())), content.getCount()));
		} else {
			throw new IllegalArgumentException("Could not parse Content from " + raw.getClass());
		}
	}

	@SuppressWarnings("deprecation")
	public static Object forgeContent2(Object raw) {
		if (raw == null) {
			return null;
		} else if (raw instanceof ITextComponent) {
			ITextComponent content = (ITextComponent) raw;
			return forge(content);
		} else if (raw instanceof HoverEvent.EntityHover) {
			HoverEvent.EntityHover content = (HoverEvent.EntityHover) raw;
			return new ChatHoverEvent.ShowEntity(forge(Registry.ENTITY_TYPE.getKey(content.type)), content.id, forge(content.name));
		} else if (raw instanceof HoverEvent.ItemHover) {
			HoverEvent.ItemHover content = (HoverEvent.ItemHover) raw;
			ItemStack itemStack = content.getItemStack();
			return new ChatHoverEvent.ShowItem(forge(Registry.ITEM.getKey(itemStack.getItem())), itemStack.getCount());
		} else {
			throw new IllegalArgumentException("Could not parse ChatContent from " + raw.getClass());
		}
	}

	public static ResourceLocation forge(ChatId id) {
		return id != null ? new ResourceLocation(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId forge(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType forge(ChatMessageType type) {
		return type != null ? ChatType.valueOf(type.name()) : null;
	}

	public static ChatMessageType forge(ChatType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static Style forgeStyle(ChatComponent component) {
		Objects.requireNonNull(component);
		Style style = Style.EMPTY;
		Color color = forgeColor(component.getColorRaw());
		if (color != null) {
			style = style.withColor(color);
		}
		for (Map.Entry<ChatComponentFormat, Boolean> entry : component.getFormatsRaw().entrySet()) {
			Boolean isSetted = entry.getValue();
			if (isSetted != null && isSetted) {
				style = style.applyFormat(forge(entry.getKey().asTextFormat()));
			}
		}
		ChatClickEvent clickEvent = component.getClickEvent();
		if (clickEvent != null) style = style.withClickEvent(forge(clickEvent));
		ChatHoverEvent<?> hoverEvent = component.getHoverEvent();
		if (hoverEvent != null) style = style.withHoverEvent(forge(hoverEvent));
		return style;
	}

	public static TextFormatting forge(ChatTextFormat format) {
		return format != null ? TextFormatting.getByCode(format.getChar()) : null;
	}

	public static ChatTextFormat forge(TextFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByChar(formatting.toString().charAt(1)) : null;
	}

	public static Color forgeColor(ChatTextColor color) {
		return color != null ? Color.fromRgb(color.value()) : null;
	}

	public static ChatTextColor forgeColor(Color color) {
		return color != null ? ChatTextColor.color(color.getValue(), null) : null;
	}

	public ChatPlayer toChatPlayer(ServerPlayerEntity player) {
		return player != null ? new ForgeChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new ForgeChatOfflinePlayer(this, server, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(ICommandSource commandSource) {
		if (commandSource instanceof ServerPlayerEntity) {
			return this.toChatPlayer((ServerPlayerEntity) commandSource);
		}
		return commandSource != null ? new ForgeChatCommandSender(this, commandSource) : null;
	}

	public ChatTeam toChatTeam(ScorePlayerTeam team) {
		return team != null ? new ForgeChatTeam(this, team) : null;
	}
}