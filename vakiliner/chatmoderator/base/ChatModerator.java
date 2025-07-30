package vakiliner.chatmoderator.base;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatmoderator.core.AutoModeration;
import vakiliner.chatmoderator.core.AutoModerationRule;
import vakiliner.chatmoderator.core.MuteManager;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.AutoModerationRule.TriggerType;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public abstract class ChatModerator {
	public static final String ID = "chatmoderator";
	public final MuteManager mutes = new MuteManager(this);
	public final AutoModeration automod = new AutoModeration(this);

	public abstract Config getConfig();

	protected abstract File getDataFolder();

	public abstract Path getConfigPath();

	public Path getMutesPath() {
		return this.getDataFolder().toPath().resolve("mutes.json");
	}

	public Path getAutoModerationRulesPath() {
		return this.getDataFolder().toPath().resolve("auto_moderation_rules.json");
	}

	public Path getAutoModerationDictionaryPath() {
		String name = this.getConfig().dictionaryFile();
		return name != null ? this.getDataFolder().toPath().resolve(name) : null;
	}

	public void broadcast(ChatComponent component) {
		this.broadcast(component, false);
	}

	public abstract void broadcast(ChatComponent component, boolean admins);

	protected abstract void spectatorsChat(ChatTranslateComponent component);

	public abstract Collection<ChatPlayer> getOnlinePlayers();

	public void onChat(ChatPlayer player, final String fullMessage, Runnable cancel) {
		boolean isCommand = fullMessage.startsWith("/");
		final String message;
		if (!isCommand) {
			message = fullMessage;
		} else compile: {
			int i = fullMessage.indexOf(' ');
			if (i == -1) i = fullMessage.length();
			final String fullCommand = fullMessage.substring(1, i);
			final String commandName;
			final Boolean pluginCommand;
			i = fullCommand.indexOf(':');
			if (i == -1) {
				pluginCommand = null;
				commandName = fullCommand.substring(0).toLowerCase();
			} else {
				String pluginName = fullCommand.substring(0, i);
				if (pluginName.equalsIgnoreCase("minecraft")) {
					pluginCommand = Boolean.FALSE;
				} else if (pluginName.equalsIgnoreCase(ID)) {
					pluginCommand = Boolean.TRUE;
				} else return;
				commandName = fullCommand.substring(Math.min(i + 1, fullCommand.length())).toLowerCase();
			}
			if (pluginCommand != Boolean.TRUE) {
				switch (commandName) {
					case "msg":
					case "tell":
					case "w":
						i = fullMessage.indexOf(' ', fullCommand.length() + 2);
						if (i == -1) i = fullMessage.length();
						String recipient = fullMessage.substring(Math.min(fullCommand.length() + 2, fullMessage.length()), i);
						if (recipient.startsWith("@")) {
							player.sendMessage(new ChatTranslateComponent("Selector not allowed", "argument.entity.selector.not_allowed", ChatNamedColor.RED));
							cancel.run();
							return;
						}
						message = fullMessage.substring(Math.min(i + 1, fullMessage.length()));
						break compile;
					case "teammsg":
					case "tm":
					case "me":
						message = fullMessage.substring(Math.min(fullCommand.length() + 2, fullMessage.length()));
						break compile;
					default:
						if (pluginCommand != null) return;
						break;
				}
			}
			if (pluginCommand != Boolean.FALSE) {
				switch (commandName) {
					case "all":
						message = fullMessage.substring(Math.min(fullCommand.length() + 2, fullMessage.length()));
						break compile;
					default:
						if (pluginCommand != null) return;
						break;
				}
			}
			return;
		}
		boolean spectatorsChat = this.getConfig().spectatorsChat() && player.getGameMode() == ChatGameMode.SPECTATOR;
		String cancelReason = spectatorsChat && isCommand ? "Наблюдатели не могут использовать команды отправки сообщений" : null;
		if (cancelReason == null) cancelReason = this.checkMessage(player, message);
		if (cancelReason != null) {
			cancel.run();
			final ChatComponent messageComponent;
			if (!isCommand) {
				messageComponent = new ChatTranslateComponent("<%s> %s", "chat.type.text", ChatTextComponent.selector(player), new ChatTextComponent(fullMessage));
			} else {
				messageComponent = new ChatTextComponent(fullMessage);
			}
			messageComponent.setColor(ChatNamedColor.RED);
			player.sendMessage(messageComponent, ChatMessageType.CHAT, player.getUniqueId());
			ChatTextComponent error = new ChatTextComponent(ChatNamedColor.RED);
			if (cancelReason.startsWith("custom:")) {
				error.setText(cancelReason.replaceFirst("^custom:", ""));
			} else {
				error.setText("[" + "ChatModerator" + "] ");
				if (!isCommand) {
					error.append(new ChatTextComponent("Ваше сообщение не было отправлено"));
				} else {
					error.append(new ChatTextComponent("Не удалось использовать команду"));
				}
				if (!cancelReason.isEmpty()) {
					error.append(new ChatTextComponent(": "));
					error.append(new ChatTextComponent(cancelReason));
				}
			}
			player.sendMessage(error);
		}
		if (spectatorsChat) {
			cancel.run();
			this.spectatorsChat(new ChatTranslateComponent("<%s> %s", "chat.type.text", ChatTextComponent.selector(player), new ChatTextComponent(fullMessage)));
		}
	}

	private String checkMessage(ChatPlayer player, String message) {
		if (player.isBypassModeration()) return null;
		Date now = new Date();
		boolean isBypassMutes = player.isBypassMutes();
		MutedPlayer mute = !isBypassMutes ? player.getMute(false) : null;
		if (mute != null) {
			Date expiration = mute.getExpirationAt();
			if (expiration == null || !expiration.before(now)) {
				StringBuilder builder = new StringBuilder("Вы были ограничены");
				String reason = mute.getReason();
				if (reason != null) {
					builder.append("\nПричина: ");
					builder.append(reason);
				}
				if (expiration != null) {
					long leftMillis = expiration.getTime() - now.getTime();
					long leftSeconds = leftMillis / 1_000;
					long leftMinutes = leftSeconds / 60;
					long leftHours = leftMinutes / 60;
					long leftDays = leftHours / 24;
					short millis = (short) (leftMillis % 1_000);
					byte seconds = (byte) (leftSeconds % 60);
					byte minutes = (byte) (leftMinutes % 60);
					byte hours = (byte) (leftHours % 24);
					short days = (short) leftDays;
					builder.append("\nЭто ограничение будет снято через");
					if (days > 0) time(builder, days, "день", "дня", "дней");
					if (hours > 0) time(builder, hours, "час", "часа", "часов");
					if (minutes > 0) time(builder, minutes, "минуту", "минуты", "минут");
					if (seconds > 0 || days == 0 && hours == 0 && minutes == 0) time(builder, minutes > 0 ? seconds : (float) seconds + ((float) millis / 1_000), "секунду", "секунды", "секунд");
				}
				return builder.toString();
			}
		}
		if (message.length() > this.getConfig().maxMessageLength()) {
			return "Слишком длинное сообщение";
		}
		if (!isBypassMutes && this.getConfig().autoModerationEnabled()) {
			final CheckResult checkResult;
			if (this.getConfig().autoModerationUseThreadPool()) {
				try {
					checkResult = this.automod.checkMessageInThreadPool(message, TriggerType.MESSAGE);
				} catch (InterruptedException err) {
					Thread.currentThread().interrupt();
					return null;
				}
			} else {
				checkResult = this.automod.checkMessage(message, TriggerType.MESSAGE);
			}
			if (checkResult.isTriggered()) {
				if (checkResult.muteTime() != null) {
					AutoModerationRule rule = checkResult.muteTime();
					this.mutes.mute(player, rule.getName(), ModeratorType.AUTOMOD, rule.getActions().muteTime(), null);
				}
				if (checkResult.logAdmins() != null) {
					@SuppressWarnings("unused")
					AutoModerationRule rule = checkResult.logAdmins();
					ChatTextComponent log = new ChatTextComponent("Заблокировано сообщение от игрока ");
					log.append(ChatTextComponent.selector(player));
					log.append(new ChatTextComponent(", "));
					ChatTextComponent showMessage = new ChatTextComponent("показать сообщение");
					showMessage.setUnderlined(true);
					showMessage.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent(message)));
					log.append(showMessage);
					ChatTranslateComponent component = new ChatTranslateComponent("[%s: %s]", "chat.type.admin", ChatNamedColor.GRAY, new ChatTextComponent("AutoMod"), log);
					this.broadcast(component, true);
				}
				if (checkResult.blockAction() != null) {
					AutoModerationRule rule = checkResult.blockAction();
					String customBlockMessage = rule.getActions().customBlockMessage();
					return "custom:[AutoMod] " + (customBlockMessage == null ? "Публикация невозможна, поскольку сообщение содержит материалы, заблокированные этим сервером. Владельцы сервера также могут просматривать содержимое сообщений." : "Содержимое сообщения заблокировано сервером. Сообщение от модераторов: «" + customBlockMessage + "»");
				}
			}
		}
		return null;
	}

	private static void time(StringBuilder builder, float time, String one, String two, String some) {
		if (time > 100) time = (float) (int) (time * 1) / 1;
		else if (time > 10) time = (float) (int) (time * 10) / 10;
		else time = (float) (int) (time * 100) / 100;
		builder.append(' ');
		builder.append(time % 1 == 0 ? Integer.toString((int) time) : Float.toString(time));
		builder.append(' ');
		if (time % 1 != 0) {
			builder.append(some);
			return;
		}
		int number = (int) time % 100;
		if (number > 10 && number < 15) {
			builder.append(some);
			return;
		}
		switch (number % 10) {
			case 1:
				builder.append(one);
				return;
			case 2:
			case 3:
			case 4:
				builder.append(two);
				return;
			default:
				builder.append(some);
				return;
		}
	}
}