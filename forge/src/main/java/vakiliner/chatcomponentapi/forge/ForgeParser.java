package vakiliner.chatcomponentapi.forge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.SelectorTextComponent;
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
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.forge.mixin.StyleAccessor;

public class ForgeParser extends BaseParser {
	private static final IStyleParser STYLE_PARSER;
	private static final Constructor<SChatPacket> S_CHAT_PACKET_CONSTRUCTOR;
	private static final UUID NIL_UUID;
	private static final Method SEND_MESSAGE_WITH_TYPE;
	private static final Method SEND_MESSAGE_WITHOUT_TYPE;
	private static final Method SET_STYLE;
	private static final Method APPEND;

	static {
		UUID nilUUID;
		try {
			nilUUID = (UUID) net.minecraft.util.Util.class.getField("NIL_UUID").get(null);
		} catch (NoSuchFieldException err) {
			nilUUID = null;
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		}
		NIL_UUID = nilUUID;
		IStyleParser parser;
		try {
			parser = new HoverEventContents();
		} catch (NoClassDefFoundError err) {
			parser = new OldStyle();
		}
		STYLE_PARSER = parser;
		Constructor<SChatPacket> SChatPacketConstructor;
		try {
			SChatPacketConstructor = SChatPacket.class.getConstructor(TextComponent.class, ChatType.class, UUID.class);
		} catch (NoSuchMethodException a) {
			try {
				SChatPacketConstructor = SChatPacket.class.getConstructor(TextComponent.class, ChatType.class);
			} catch (NoSuchMethodException err) {
				throw new IllegalStateException(err);
			}
		}
		S_CHAT_PACKET_CONSTRUCTOR = SChatPacketConstructor;
		try {
			SET_STYLE = TextComponent.class.getMethod("method_10862", Style.class);
			APPEND = TextComponent.class.getMethod("method_10852", TextComponent.class);
		} catch (NoSuchMethodException err) {
			throw new RuntimeException(err);
		}
		Method sendMessageWithType;
		try {
			sendMessageWithType = ServerPlayerEntity.class.getMethod("method_14254", TextComponent.class, ChatType.class, UUID.class);
		} catch (NoSuchMethodException e) {
			try {
				sendMessageWithType = ServerPlayerEntity.class.getMethod("method_14254", TextComponent.class, ChatType.class);
			} catch (NoSuchMethodException err) {
				throw new RuntimeException(err);
			}
		}
		SEND_MESSAGE_WITH_TYPE = sendMessageWithType;
		Method sendMessageWithoutType;
		try {
			sendMessageWithoutType = CommandSource.class.getMethod("method_9203", TextComponent.class, UUID.class);
		} catch (NoSuchMethodException e) {
			try {
				sendMessageWithoutType = CommandSource.class.getMethod("method_9203", TextComponent.class);
			} catch (NoSuchMethodException err) {
				throw new RuntimeException(err);
			}
		}
		SEND_MESSAGE_WITHOUT_TYPE = sendMessageWithoutType;
	}

	public boolean supportsSeparatorInSelector() {
		return false;
	}

	public boolean supportsFontInStyle() {
		return true;
	}

	public void sendMessage(ICommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = NIL_UUID;
		ITextComponent component = forge(chatComponent, commandSource instanceof MinecraftServer);
		try {
			if (commandSource instanceof ServerPlayerEntity) {
				if (SEND_MESSAGE_WITH_TYPE.getParameterTypes().length == 3) {
					SEND_MESSAGE_WITH_TYPE.invoke(commandSource, component, forge(type), uuid);
				} else {
					SEND_MESSAGE_WITH_TYPE.invoke(commandSource, component, forge(type));
				}
			} else {
				if (SEND_MESSAGE_WITHOUT_TYPE.getParameterTypes().length == 2) {
					SEND_MESSAGE_WITHOUT_TYPE.invoke(commandSource, component, uuid);
				} else {
					SEND_MESSAGE_WITHOUT_TYPE.invoke(commandSource, component);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = NIL_UUID;
		this.sendMessage(playerList.getServer(), component, type, uuid);
		final SChatPacket packet;
		try {
			if (S_CHAT_PACKET_CONSTRUCTOR.getParameterTypes().length == 3) {
				packet = S_CHAT_PACKET_CONSTRUCTOR.newInstance(forge(component), forge(type), uuid);
			} else {
				packet = S_CHAT_PACKET_CONSTRUCTOR.newInstance(forge(component), forge(type));
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException err) {
			throw new RuntimeException(err);
		}
		playerList.broadcastAll(packet);
	}

	public void execute(MinecraftServer server, IChatPlugin plugin, Runnable runnable) {
		if (plugin instanceof IForgeChatPlugin) {
			server.execute(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public static ITextComponent forge(ChatComponent raw) {
		return forge(raw, false);
	}

	public static ITextComponent forge(ChatComponent raw, boolean isConsole) {
		final TextComponent component;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new StringTextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslationTextComponent(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> forge(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorTextComponent(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse ITextComponent from " + raw.getClass());
		}
		try {
			SET_STYLE.invoke(component, forge(raw.getStyle()));
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new RuntimeException(err);
		}
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			component.append(forge(chatComponent, isConsole));
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
		} else if (raw instanceof SelectorTextComponent) {
			SelectorTextComponent component = (SelectorTextComponent) raw;
			chatComponent = new ChatSelectorComponent(component.getPattern());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setStyle(forge(raw.getStyle()));
		for (ITextComponent component : raw.getSiblings()) {
			chatComponent.append(forge(component));
		}
		return chatComponent;
	}

	public static Style forge(ChatStyle chatStyle) {
		return chatStyle != null ? STYLE_PARSER.forge(chatStyle) : null;
	}

	public static ChatStyle forge(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		StyleAccessor accessor = (StyleAccessor) style;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(STYLE_PARSER.injectColor(style));
		builder.withBold(accessor.getBold());
		builder.withItalic(accessor.getItalic());
		builder.withUnderlined(accessor.getUnderlined());
		builder.withStrikethrough(accessor.getStrikethrough());
		builder.withObfuscated(accessor.getObfuscated());
		builder.withClickEvent(forge(accessor.getClickEvent()));
		builder.withHoverEvent(forge(accessor.getHoverEvent()));
		builder.withInsertion(accessor.getInsertion());
		builder.withFont(forge(accessor.getFont()));
		return builder.build();
	}

	public static ClickEvent forge(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent forge(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static HoverEvent forge(ChatHoverEvent<?> event) {
		return event != null ? STYLE_PARSER.forge(event) : null;
	}

	public static ChatHoverEvent<?> forge(HoverEvent event) {
		return event != null ? STYLE_PARSER.forge(event) : null;
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

	public static TextFormatting forge(ChatTextFormat format) {
		return format != null ? TextFormatting.getByName(format.name()) : null;
	}

	public static ChatTextFormat forge(TextFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByName(formatting.getName()) : null;
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

	public ChatServer toChatServer(MinecraftServer server) {
		return server != null ? new ForgeChatServer(this, server) : null;
	}

	public ChatPlayerList toChatPlayerList(PlayerList playerList) {
		return playerList != null ? new ForgeChatPlayerList(this, playerList) : null;
	}
}