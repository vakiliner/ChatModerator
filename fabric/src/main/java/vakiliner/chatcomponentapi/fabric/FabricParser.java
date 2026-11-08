package vakiliner.chatcomponentapi.fabric;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.PlayerTeam;
import vakiliner.chatcomponentapi.base.BaseParser;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.ChatTeam;
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

public class FabricParser extends BaseParser {
	private static final IStyleParser STYLE_PARSER;
	private static final Constructor<ClientboundChatPacket> CHAT_PACKET_CONSTRUCTOR;
	private static final UUID NIL_UUID;
	private static final Method PLAYER_SEND_MESSAGE;
	private static final Method COMMAND_SOURCE_SEND_MESSAGE;
	private static final Method SET_STYLE;
	private static final Method APPEND;
	private static final Field FONT;

	static {
		try {
			SET_STYLE = BaseComponent.class.getMethod("method_10862", Style.class);
			APPEND = BaseComponent.class.getMethod("method_10852", Component.class);
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		UUID nilUUID;
		try {
			nilUUID = (UUID) net.minecraft.Util.class.getField("field_25140").get(null);
		} catch (NoSuchFieldException err) {
			nilUUID = null;
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		}
		NIL_UUID = nilUUID;
		Field font;
		try {
			font = Style.class.getDeclaredField("font");
			font.setAccessible(true);
		} catch (NoSuchFieldException err) {
			font = null;
		}
		FONT = font;
		IStyleParser parser;
		try {
			parser = new HoverEventContents();
		} catch (Throwable err) {
			parser = new OldStyle();
		}
		STYLE_PARSER = parser;
		Constructor<ClientboundChatPacket> сhatPacketConstructor;
		try {
			сhatPacketConstructor = ClientboundChatPacket.class.getConstructor(Component.class, ChatType.class, UUID.class);
		} catch (NoSuchMethodException a) {
			try {
				сhatPacketConstructor = ClientboundChatPacket.class.getConstructor(Component.class, ChatType.class);
			} catch (NoSuchMethodException err) {
				throw new IllegalStateException(err);
			}
		}
		CHAT_PACKET_CONSTRUCTOR = сhatPacketConstructor;
		Method playerSendMessage;
		try {
			playerSendMessage = ServerPlayer.class.getMethod("method_14254", Component.class, ChatType.class, UUID.class);
		} catch (NoSuchMethodException e) {
			try {
				playerSendMessage = ServerPlayer.class.getMethod("method_14254", Component.class, ChatType.class);
			} catch (NoSuchMethodException err) {
				throw new IllegalStateException(err);
			}
		}
		PLAYER_SEND_MESSAGE = playerSendMessage;
		Method commandSourceSendMessage;
		try {
			commandSourceSendMessage = CommandSource.class.getMethod("method_9203", Component.class, UUID.class);
		} catch (NoSuchMethodException e) {
			try {
				commandSourceSendMessage = CommandSource.class.getMethod("method_9203", Component.class);
			} catch (NoSuchMethodException err) {
				throw new IllegalStateException(err);
			}
		}
		COMMAND_SOURCE_SEND_MESSAGE = commandSourceSendMessage;
	}

	@Override
	public boolean supportsSeparatorInSelector() {
		return false;
	}

	@Override
	public boolean supportsFontInStyle() {
		return FONT != null;
	}

	public void sendMessage(CommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = NIL_UUID;
		Component component = fabric(chatComponent, commandSource instanceof MinecraftServer);
		try {
			if (commandSource instanceof ServerPlayer) {
				if (PLAYER_SEND_MESSAGE.getParameterTypes().length == 3) {
					PLAYER_SEND_MESSAGE.invoke(commandSource, component, fabric(type), uuid);
				} else {
					PLAYER_SEND_MESSAGE.invoke(commandSource, component, fabric(type));
				}
			} else {
				if (COMMAND_SOURCE_SEND_MESSAGE.getParameterTypes().length == 2) {
					COMMAND_SOURCE_SEND_MESSAGE.invoke(commandSource, component, uuid);
				} else {
					COMMAND_SOURCE_SEND_MESSAGE.invoke(commandSource, component);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
	}

	public void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid) {
		this.broadcastMessage(playerList, component, type, uuid, null);
	}

	protected void broadcastMessage(PlayerList playerList, ChatComponent component, ChatMessageType type, UUID uuid, Predicate<? super ChatPlayer> predicate) {
		if (uuid == null) uuid = NIL_UUID;
		final ClientboundChatPacket packet;
		try {
			if (CHAT_PACKET_CONSTRUCTOR.getParameterTypes().length == 3) {
				packet = CHAT_PACKET_CONSTRUCTOR.newInstance(fabric(component), fabric(type), uuid);
			} else {
				packet = CHAT_PACKET_CONSTRUCTOR.newInstance(fabric(component), fabric(type));
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException err) {
			throw new IllegalStateException(err);
		}
		this.sendMessage(playerList.getServer(), component, type, uuid);
		if (predicate == null) {
			playerList.broadcastAll(packet);
		} else for (ServerPlayer player : playerList.getPlayers()) {
			if (predicate.test(this.toChatPlayer(player))) {
				player.connection.send(packet);
			}
		}
	}

	public static Component fabric(ChatComponent raw) {
		return fabric(raw, false);
	}

	public static Component fabric(ChatComponent raw, boolean isConsole) {
		if (raw == null) return null;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		final BaseComponent component;
		if (raw instanceof ChatTextComponent) {
			ChatTextComponent chatComponent = (ChatTextComponent) raw;
			component = new TextComponent(chatComponent.getText());
		} else if (raw instanceof ChatTranslateComponent) {
			ChatTranslateComponent chatComponent = (ChatTranslateComponent) raw;
			component = new TranslatableComponent(chatComponent.getKey(), chatComponent.getWith().stream().map((c) -> fabric(c, isConsole)).toArray());
		} else if (raw instanceof ChatSelectorComponent) {
			ChatSelectorComponent chatComponent = (ChatSelectorComponent) raw;
			component = new SelectorComponent(chatComponent.getSelector());
		} else {
			throw new IllegalArgumentException("Could not parse Component from " + raw.getClass());
		}
		try {
			SET_STYLE.invoke(component, fabric(raw.getStyle()));
		} catch (IllegalAccessException | InvocationTargetException err) {
			throw new IllegalStateException(err);
		}
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			try {
				APPEND.invoke(component, fabric(chatComponent, isConsole));
			} catch (IllegalAccessException | InvocationTargetException err) {
				throw new IllegalStateException(err);
			}
		}
		return component;
	}

	public static ChatComponent fabric(Component raw) {
		if (raw == null) return null;
		final ChatComponent chatComponent;
		if (raw instanceof TextComponent) {
			TextComponent component = (TextComponent) raw;
			chatComponent = new ChatTextComponent(component.getText());
		} else if (raw instanceof TranslatableComponent) {
			TranslatableComponent component = (TranslatableComponent) raw;
			chatComponent = new ChatTranslateComponent(null, component.getKey(), Arrays.stream(component.getArgs()).map((arg) -> arg instanceof Component ? fabric((Component) arg) : new ChatTextComponent(String.valueOf(arg))).collect(Collectors.toList()));
		} else if (raw instanceof SelectorComponent) {
			SelectorComponent component = (SelectorComponent) raw;
			chatComponent = new ChatSelectorComponent(component.getPattern());
		} else {
			throw new IllegalArgumentException("Could not parse ChatComponent from " + raw.getClass());
		}
		chatComponent.setStyle(fabric(raw.getStyle()));
		for (Component component : raw.getSiblings()) {
			chatComponent.append(fabric(component));
		}
		return chatComponent;
	}

	public static Style fabric(ChatStyle chatStyle) {
		return chatStyle != null ? STYLE_PARSER.fabric(chatStyle) : null;
	}

	public static ChatStyle fabric(Style style) {
		if (style == null) return null;
		if (style.isEmpty()) return ChatStyle.EMPTY;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(STYLE_PARSER.injectColor(style));
		builder.withBold(style.bold);
		builder.withItalic(style.italic);
		builder.withUnderlined(style.underlined);
		builder.withStrikethrough(style.strikethrough);
		builder.withObfuscated(style.obfuscated);
		builder.withClickEvent(fabric(style.clickEvent));
		builder.withHoverEvent(fabric(style.hoverEvent));
		builder.withInsertion(style.insertion);
		if (FONT != null) try {
			builder.withFont(fabric((ResourceLocation) FONT.get(style)));
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		}
		return builder.build();
	}

	public static ClickEvent fabric(ChatClickEvent event) {
		if (event == null) return null;
		switch (event.action()) {
			case OPEN_URL: return new ClickEvent(ClickEvent.Action.OPEN_URL, event.value());
			case OPEN_FILE: return new ClickEvent(ClickEvent.Action.OPEN_FILE, event.value());
			case RUN_COMMAND: return new ClickEvent(ClickEvent.Action.RUN_COMMAND, event.value());
			case SUGGEST_COMMAND: return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, event.value());
			case CHANGE_PAGE: return new ClickEvent(ClickEvent.Action.CHANGE_PAGE, event.value());
			case COPY_TO_CLIPBOARD: return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, event.value());
			default: throw new IllegalArgumentException("Unknown ChatClickEvent.Action " + event.action());
		}
	}

	public static ChatClickEvent fabric(ClickEvent event) {
		if (event == null) return null;
		switch (event.getAction()) {
			case OPEN_URL: return new ChatClickEvent(ChatClickEvent.Action.OPEN_URL, event.getValue());
			case OPEN_FILE: return new ChatClickEvent(ChatClickEvent.Action.OPEN_FILE, event.getValue());
			case RUN_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, event.getValue());
			case SUGGEST_COMMAND: return new ChatClickEvent(ChatClickEvent.Action.SUGGEST_COMMAND, event.getValue());
			case CHANGE_PAGE: return new ChatClickEvent(ChatClickEvent.Action.CHANGE_PAGE, event.getValue());
			case COPY_TO_CLIPBOARD: return new ChatClickEvent(ChatClickEvent.Action.COPY_TO_CLIPBOARD, event.getValue());
			default: throw new IllegalArgumentException("Unknown ClickEvent.Action " + event.getAction());
		}
	}

	public static HoverEvent fabric(ChatHoverEvent<?> event) {
		return event != null ? STYLE_PARSER.fabric(event) : null;
	}

	public static ChatHoverEvent<?> fabric(HoverEvent event) {
		return event != null ? STYLE_PARSER.fabric(event) : null;
	}

	public static ResourceLocation fabric(ChatId id) {
		return id != null ? new ResourceLocation(id.namespace(), id.value()) : null;
	}

	public static ChatId fabric(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType fabric(ChatMessageType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatType.CHAT;
			case SYSTEM: return ChatType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatMessageType " + type);
		}
	}

	public static ChatMessageType fabric(ChatType type) {
		if (type == null) return null;
		switch (type) {
			case CHAT: return ChatMessageType.CHAT;
			case SYSTEM: return ChatMessageType.SYSTEM;
			default: throw new IllegalArgumentException("Unknown ChatType " + type);
		}
	}

	public static ChatFormatting fabric(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByName(format.getName()) : null;
	}

	public static ChatTextFormat fabric(ChatFormatting formatting) {
		return formatting != null ? ChatTextFormat.getByName(formatting.getName()) : null;
	}

	public ChatPlayer toChatPlayer(ServerPlayer player) {
		return player != null ? new FabricChatPlayer(this, player) : null;
	}

	public ChatOfflinePlayer toChatOfflinePlayer(MinecraftServer server, GameProfile gameProfile) {
		return gameProfile != null ? new FabricChatOfflinePlayer(this, server, gameProfile) : null;
	}

	public ChatCommandSender toChatCommandSender(CommandSource commandSource) {
		if (commandSource == null) return null;
		if (commandSource instanceof ServerPlayer) {
			return this.toChatPlayer((ServerPlayer) commandSource);
		} else if (commandSource instanceof MinecraftServer) {
			return this.toChatServer((MinecraftServer) commandSource);
		}
		return new FabricChatCommandSender(this, commandSource);
	}

	public ChatTeam toChatTeam(PlayerTeam team) {
		return team != null ? new FabricChatTeam(this, team) : null;
	}

	public ChatServer toChatServer(MinecraftServer server) {
		return server != null ? new FabricChatServer(this, server) : null;
	}

	public ChatPlayerList toChatPlayerList(PlayerList playerList) {
		return playerList != null ? new FabricChatPlayerList(this, playerList) : null;
	}
}
