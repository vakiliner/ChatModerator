package vakiliner.chatcomponentapi.fabric;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import vakiliner.chatcomponentapi.fabric.mixin.StyleAccessor;

public class FabricParser extends BaseParser {
	private static final IStyleParser STYLE_PARSER;
	private static final Constructor<ClientboundChatPacket> CLIENTBOUND_CHAT_PACKET_CONSTRUCTOR;
	private static final UUID NIL_UUID;
	private static final Method SEND_MESSAGE_WITH_TYPE;
	private static final Method SEND_MESSAGE_WITHOUT_TYPE;
	private static final Method SET_STYLE;
	private static final Method APPEND;

	static {
		UUID nilUUID;
		try {
			nilUUID = (UUID) net.minecraft.Util.class.getField("NIL_UUID").get(null);
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
		Constructor<ClientboundChatPacket> clientboundChatPacketConstructor;
		try {
			clientboundChatPacketConstructor = ClientboundChatPacket.class.getConstructor(Component.class, ChatType.class, UUID.class);
		} catch (NoSuchMethodException a) {
			try {
				clientboundChatPacketConstructor = ClientboundChatPacket.class.getConstructor(Component.class, ChatType.class);
			} catch (NoSuchMethodException err) {
				throw new IllegalStateException(err);
			}
		}
		CLIENTBOUND_CHAT_PACKET_CONSTRUCTOR = clientboundChatPacketConstructor;
		try {
			SET_STYLE = BaseComponent.class.getMethod("method_10862", Style.class);
			APPEND = BaseComponent.class.getMethod("method_10852", Component.class);
		} catch (NoSuchMethodException err) {
			throw new RuntimeException(err);
		}
		Method sendMessageWithType;
		try {
			sendMessageWithType = ServerPlayer.class.getMethod("method_14254", Component.class, ChatType.class, UUID.class);
		} catch (NoSuchMethodException e) {
			try {
				sendMessageWithType = ServerPlayer.class.getMethod("method_14254", Component.class, ChatType.class);
			} catch (NoSuchMethodException err) {
				throw new RuntimeException(err);
			}
		}
		SEND_MESSAGE_WITH_TYPE = sendMessageWithType;
		Method sendMessageWithoutType;
		try {
			sendMessageWithoutType = CommandSource.class.getMethod("method_9203", Component.class, UUID.class);
		} catch (NoSuchMethodException e) {
			try {
				sendMessageWithoutType = CommandSource.class.getMethod("method_9203", Component.class);
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

	public void sendMessage(CommandSource commandSource, ChatComponent chatComponent, ChatMessageType type, UUID uuid) {
		if (uuid == null) uuid = NIL_UUID;
		Component component = fabric(chatComponent, commandSource instanceof MinecraftServer);
		try {
			if (commandSource instanceof ServerPlayer) {
				if (SEND_MESSAGE_WITH_TYPE.getParameterTypes().length == 3) {
					SEND_MESSAGE_WITH_TYPE.invoke(commandSource, component, fabric(type), uuid);
				} else {
					SEND_MESSAGE_WITH_TYPE.invoke(commandSource, component, fabric(type));
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
		final ClientboundChatPacket packet;
		try {
			if (CLIENTBOUND_CHAT_PACKET_CONSTRUCTOR.getParameterTypes().length == 3) {
				packet = CLIENTBOUND_CHAT_PACKET_CONSTRUCTOR.newInstance(fabric(component), fabric(type), uuid);
			} else {
				packet = CLIENTBOUND_CHAT_PACKET_CONSTRUCTOR.newInstance(fabric(component), fabric(type));
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException err) {
			throw new RuntimeException(err);
		}
		playerList.broadcastAll(packet);
	}

	public void execute(MinecraftServer server, IChatPlugin plugin, Runnable runnable) {
		if (plugin instanceof IFabricChatPlugin) {
			server.execute(runnable);
		} else {
			throw new ClassCastException("Invalid plugin");
		}
	}

	public static Component fabric(ChatComponent raw) {
		return fabric(raw, false);
	}

	public static Component fabric(ChatComponent raw, boolean isConsole) {
		final BaseComponent component;
		if (raw instanceof ChatComponentModified) {
			raw = ((ChatComponentModified) raw).getComponent(isConsole);
		}
		if (raw == null) {
			return null;
		} else if (raw instanceof ChatTextComponent) {
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
			throw new RuntimeException(err);
		}
		List<ChatComponent> extra = raw.getExtra();
		if (extra != null) for (ChatComponent chatComponent : extra) {
			try {
				APPEND.invoke(component, fabric(chatComponent, isConsole));
			} catch (IllegalAccessException | InvocationTargetException err) {
				throw new RuntimeException(err);
			}
		}
		return component;
	}

	public static ChatComponent fabric(Component raw) {
		final ChatComponent chatComponent;
		if (raw == null) {
			return null;
		} else if (raw instanceof TextComponent) {
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
		StyleAccessor accessor = (StyleAccessor) style;
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(STYLE_PARSER.injectColor(style));
		builder.withBold(accessor.getBold());
		builder.withItalic(accessor.getItalic());
		builder.withUnderlined(accessor.getUnderlined());
		builder.withStrikethrough(accessor.getStrikethrough());
		builder.withObfuscated(accessor.getObfuscated());
		builder.withClickEvent(fabric(accessor.getClickEvent()));
		builder.withHoverEvent(fabric(accessor.getHoverEvent()));
		builder.withInsertion(accessor.getInsertion());
		builder.withFont(fabric(accessor.getFont()));
		return builder.build();
	}

	public static ClickEvent fabric(ChatClickEvent event) {
		return event != null ? new ClickEvent(ClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static ChatClickEvent fabric(ClickEvent event) {
		return event != null ? new ChatClickEvent(ChatClickEvent.Action.getByName(event.getAction().getName()), event.getValue()) : null;
	}

	public static HoverEvent fabric(ChatHoverEvent<?> event) {
		return event != null ? STYLE_PARSER.fabric(event) : null;
	}

	public static ChatHoverEvent<?> fabric(HoverEvent event) {
		return event != null ? STYLE_PARSER.fabric(event) : null;
	}

	public static ResourceLocation fabric(ChatId id) {
		return id != null ? new ResourceLocation(id.getNamespace(), id.getValue()) : null;
	}

	public static ChatId fabric(ResourceLocation resourceLocation) {
		return resourceLocation != null ? new ChatId(resourceLocation.getNamespace(), resourceLocation.getPath()) : null;
	}

	public static ChatType fabric(ChatMessageType type) {
		return type != null ? ChatType.valueOf(type.name()) : null;
	}

	public static ChatMessageType fabric(ChatType type) {
		return type != null ? ChatMessageType.valueOf(type.name()) : null;
	}

	public static ChatFormatting fabric(ChatTextFormat format) {
		return format != null ? ChatFormatting.getByName(format.name()) : null;
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
		if (commandSource instanceof ServerPlayer) {
			return this.toChatPlayer((ServerPlayer) commandSource);
		}
		return commandSource != null ? new FabricChatCommandSender(this, commandSource) : null;
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