package vakiliner.chatmoderator.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import vakiliner.chatcomponentapi.common.ChatGameMode;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.util.Utils;
import vakiliner.chatmoderator.core.AutoModeration;
import vakiliner.chatmoderator.core.AutoModerationRule;
import vakiliner.chatmoderator.core.MuteManager;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.AutoModerationRule.Actions;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public abstract class ChatModerator {
	private static final Pattern QUALIFIER = Pattern.compile("^(?<q>test|alpha|beta|pre|rc)(?<n>\\d+)$");
	public static final String ID = "chatmoderator";
	public final MuteManager mutes = new MuteManager(this);
	public final AutoModeration automod = new AutoModeration(this);

	protected void init(ILoader loader) {
		loader.saveDefaultConfig();
		if (!this.getAutoModerationRulesPath().toFile().exists()) {
			loader.saveResource("auto_moderation_rules.json", false);
		}
		loader.reloadConfig();
		String version = this.getVersion();
		String configVersion = this.getConfig().version();
		int v = compareVersion(version, configVersion);
		if (v < 0) throw new IllegalStateException("Unsupported config version " + configVersion);
		if (v > 0) {
			loader.log("Updating config to new version " + version);
			if (compareVersion(version, "1.0.3") < 0) {
				this.getConfig().showFailMessage(true);
				Map<String, String> messages = new HashMap<>();
				messages.put("fail_send_message", "Ваше сообщение не было отправлено");
				messages.put("fail_send_command", "Не удалось использовать команду");
				messages.put("fail_reasons.muted", "Вы были ограничены");
				messages.put("fail_reasons.muted_with_reason", "Вы были ограничены\nПричина: %s");
				messages.put("fail_reasons.cannot_use_msg_command_in_spectator", "Наблюдатели не могут использовать команды отправки сообщений");
				messages.put("fail_reasons.long_message", "Слишком длинное сообщение");
				messages.put("fail_reasons.automod_blocked_without_custom_message", "Публикация невозможна, поскольку сообщение содержит материалы, заблокированные этим сервером. Владельцы сервера также могут просматривать содержимое сообщений.");
				messages.put("fail_reasons.automod_blocked_with_custom_message", "Содержимое сообщения заблокировано сервером. Сообщение от модераторов: «%s»");
				this.getConfig().messages(messages);
				Path getAutoModerationRulesPath = this.getAutoModerationRulesPath();
				if (getAutoModerationRulesPath.toFile().exists()) {
					final JsonArray array;
					try {
						array = new Gson().fromJson(new InputStreamReader(Files.newInputStream(getAutoModerationRulesPath), StandardCharsets.UTF_8), JsonArray.class);
					} catch (IOException err) {
						throw new RuntimeException(err);
					}
					for (JsonElement element : array) {
						JsonObject actions = element.getAsJsonObject().getAsJsonObject("actions");
						JsonElement blockActionRaw = actions.get("block_action");
						JsonElement customBlockMessage = actions.remove("custom_block_message");
						if (blockActionRaw != null && !blockActionRaw.isJsonNull()) {
							JsonPrimitive blockAction = blockActionRaw.getAsJsonPrimitive();
							if (blockActionRaw.getAsJsonPrimitive().isBoolean()) {
								actions.addProperty("block_action", blockAction.getAsBoolean() ? customBlockMessage != null && !customBlockMessage.isJsonNull() ? customBlockMessage.getAsString() : "" : null);
							}
						}
					}
					try {
						Files.write(getAutoModerationRulesPath, new Gson().toJson(array).getBytes(StandardCharsets.UTF_8));
					} catch (IOException err) {
						throw new RuntimeException(err);
					}
				}
			}
			this.getConfig().version(version);
			loader.saveConfig();
		}
		String dictionaryFile = this.getConfig().dictionaryFile();
		if (dictionaryFile != null && dictionaryFile.equals("dictionary_ru.json")) {
			if (!this.getAutoModerationDictionaryPath().toFile().exists()) {
				loader.saveResource("dictionary_ru.json", false);
			}
		}
		try {
			this.automod.reload();
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	public abstract Config getConfig();

	protected abstract File getDataFolder();

	public abstract Path getConfigPath();

	public abstract String getVersion();

	public abstract String getName();

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
		String cancelReason = spectatorsChat && isCommand ? this.getConfig().message("fail_reasons.cannot_use_msg_command_in_spectator") : null;
		if (cancelReason == null) cancelReason = this.checkMessage(player, message);
		if (cancelReason != null) {
			cancel.run();
			if (this.getConfig().showFailMessage()) {
				final ChatComponent messageComponent;
				if (!isCommand) {
					messageComponent = new ChatTranslateComponent("<%s> %s", "chat.type.text", ChatTextComponent.selector(player), new ChatTextComponent(fullMessage));
				} else {
					messageComponent = new ChatTextComponent(fullMessage);
				}
				messageComponent.setColor(ChatNamedColor.RED);
				player.sendMessage(messageComponent, ChatMessageType.CHAT, player.getUniqueId());
			}
			ChatTextComponent error = new ChatTextComponent(ChatNamedColor.RED);
			if (cancelReason.startsWith("custom:")) {
				error.setText(cancelReason.replaceFirst("^custom:", ""));
			} else {
				error.setText("[" + this.getName() + "] ");
				if (!isCommand) {
					error.append(new ChatTextComponent(this.getConfig().message("fail_send_message")));
				} else {
					error.append(new ChatTextComponent(this.getConfig().message("fail_send_command")));
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
				StringBuilder builder = new StringBuilder();
				String reason = mute.getReason();
				if (reason == null) {
					builder.append(this.getConfig().message("fail_reasons.muted"));
				} else {
					builder.append(Utils.stringFormat(this.getConfig().message("fail_reasons.muted_with_reason"), reason));
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
		int maxMessageLength = this.getConfig().maxMessageLength();
		if (message.length() > maxMessageLength) {
			return Utils.stringFormat(this.getConfig().message("fail_reasons.long_message"), maxMessageLength, message.length());
		}
		if (!isBypassMutes && this.getConfig().autoModerationEnabled()) {
			final CheckResult checkResult;
			if (this.getConfig().autoModerationUseThreadPool()) {
				try {
					checkResult = this.automod.checkMessageInThreadPool(message);
				} catch (InterruptedException err) {
					Thread.currentThread().interrupt();
					return null;
				}
			} else {
				checkResult = this.automod.checkMessage(message);
			}
			if (checkResult.isTriggered()) {
				Actions actions = new Actions();
				for (AutoModerationRule rule : checkResult.getTriggeredRules().keySet()) {
					Actions ruleActions = rule.getActions();
					String blockAction = ruleActions.blockAction();
					int muteTime = ruleActions.muteTime();
					boolean logAdmins = ruleActions.logAdmins();
					if (actions.blockAction() == null && blockAction != null) {
						actions.blockAction(blockAction);
					}
					if (actions.muteTime() < muteTime) {
						actions.muteTime(muteTime);
					}
					if (!actions.logAdmins() && logAdmins) {
						actions.logAdmins(logAdmins);
					}
				}
				if (this.automodTrigger(player, checkResult, actions)) {
					AutoModerationRule firstRule = checkResult.getTriggeredRules().firstKey();
					String blockAction = actions.blockAction();
					int muteTime = actions.muteTime();
					boolean logAdmins = actions.logAdmins();
					if (muteTime > 0) {
						this.mutes.mute(player, firstRule.getName(), ModeratorType.AUTOMOD, muteTime, null);
					}
					if (logAdmins) {
						ChatTextComponent log = new ChatTextComponent((blockAction != null ? "Заблокировано" : "Отмечено") + " сообщение от игрока ");
						log.append(ChatTextComponent.selector(player));
						log.append(new ChatTextComponent(", "));
						ChatTextComponent showMessage = new ChatTextComponent("показать сообщение");
						showMessage.setUnderlined(true);
						showMessage.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent(checkResult.getMessage())));
						log.append(showMessage);
						ChatTranslateComponent component = new ChatTranslateComponent("[%s: %s]", "chat.type.admin", ChatNamedColor.GRAY, new ChatTextComponent("AutoMod"), log);
						this.broadcast(component, true);
					}
					if (blockAction != null) return "custom:[AutoMod] " + (blockAction.isEmpty() ? this.getConfig().message("fail_reasons.automod_blocked_without_custom_message") : Utils.stringFormat(this.getConfig().message("fail_reasons.automod_blocked_with_custom_message"), blockAction));
				}
			}
		}
		return null;
	}

	protected abstract boolean automodTrigger(ChatPlayer player, CheckResult checkResult, Actions actions);

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

	private static int compareVersion(String version1, String version2) {
		if (version1.equals(version2)) return 0;
		int i1 = version1.indexOf("-");
		int i2 = version2.indexOf("-");
		int[] ver1 = Stream.of(version1.substring(0, i1 < 0 ? version1.length() : i1).split("\\.")).mapToInt(Integer::parseInt).toArray();
		int[] ver2 = Stream.of(version2.substring(0, i2 < 0 ? version2.length() : i2).split("\\.")).mapToInt(Integer::parseInt).toArray();
		if (ver1.length != 4) ver1 = Arrays.copyOf(ver1, 4);
		if (ver2.length != 4) ver2 = Arrays.copyOf(ver2, 4);
		for (int i = 0; i < 4; i++) {
			int v1 = ver1[i];
			int v2 = ver2[i];
			if (v1 != v2) return v1 > v2 ? 1 : -1;
		}
		String qu1 = version1.substring(i1 < 0 ? version1.length() : i1 + 1);
		String qu2 = version2.substring(i2 < 0 ? version2.length() : i2 + 1);
		if (qu1.isEmpty() != qu2.isEmpty()) return qu1.isEmpty() ? 1 : -1;
		Matcher m1 = QUALIFIER.matcher(qu1);
		Matcher m2 = QUALIFIER.matcher(qu2);
		if (!m1.find()) throw new IllegalArgumentException("Invalid qualifier");
		if (!m2.find()) throw new IllegalArgumentException("Invalid qualifier");
		int q1 = qualifier(m1.group("q"));
		int q2 = qualifier(m2.group("q"));
		if (q1 != q2) return q1 > q2 ? 1 : -1;
		int n1 = Integer.parseInt(m1.group("n"));
		int n2 = Integer.parseInt(m2.group("n"));
		return n1 == n2 ? 0 : n1 > n2 ? 1 : -1;
	}

	private static int qualifier(String qualifier) {
		switch (qualifier) {
			case "test": return 0;
			case "alpha": return 1;
			case "beta": return 2;
			case "pre": return 3;
			case "rc": return 4;
			default: throw new IllegalArgumentException("Unknown qualifier " + qualifier);
		}
	}
}