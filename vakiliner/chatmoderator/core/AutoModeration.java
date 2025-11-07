package vakiliner.chatmoderator.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import com.google.gson.Gson;
import vakiliner.chatmoderator.api.GsonAutoMod;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;
import vakiliner.chatmoderator.api.GsonDictionary;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.automod.BaseAutoModerationRule;
import vakiliner.chatmoderator.core.automod.EventType;
import vakiliner.chatmoderator.core.automod.MatchResult;
import vakiliner.chatmoderator.core.automod.MessageActions;

public class AutoModeration {
	private final ChatModerator manager;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	private final List<BaseAutoModerationRule> rules = new ArrayList<>();
	public final Map<Character, String> cleaner = new HashMap<>();

	public AutoModeration(ChatModerator manager) {
		this.manager = manager;
	}

	public CheckResult checkMessage(ChatPlayer player, String message) {
		try {
			return this.check(player, message, EventType.MESSAGE, false);
		} catch (InterruptedException err) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(err);
		}
	}

	public CheckResult checkMessageInThreadPool(ChatPlayer player, String message) throws InterruptedException {
		return this.check(player, message, EventType.MESSAGE, true);
	}

	private CheckResult check(ChatPlayer player, String message, EventType triggerType, boolean useThreadPool) throws InterruptedException {
		Objects.requireNonNull(triggerType);
		ArrayList<BaseAutoModerationRule> rules = new ArrayList<>();
		Map<BaseAutoModerationRule, MatchResult> triggeredRules = triggerType == EventType.MESSAGE ? new TreeMap<>((a, b) -> ((MessageActions) b.getActions()).muteTime() - ((MessageActions) a.getActions()).muteTime()) : new HashMap<>();
		for (BaseAutoModerationRule rule : this.rules) {
			if (rule.isEnabled() && rule.getEventType() == triggerType) rules.add(rule);
		}
		CheckResult checkResult = new CheckResult(rules, triggeredRules, message);
		Consumer<BaseAutoModerationRule> consumer = (rule) -> {
			MatchResult matchResult = rule.checkText(player, checkResult.getMessage());
			if (matchResult != null) synchronized (triggeredRules) {
				triggeredRules.put(rule, matchResult);
			}
		};
		int size = checkResult.rules.size();
		if (useThreadPool) {
			List<Future<?>> waitlist = new ArrayList<>();
			int index = 0;
			for (BaseAutoModerationRule rule : checkResult.rules) {
				if (size > ++index) {
					waitlist.add(this.threadPool.submit(() -> consumer.accept(rule)));
				} else {
					consumer.accept(rule);
				}
			}
			for (Future<?> future : waitlist) {
				try {
					future.get();
				} catch (ExecutionException err) {
					throw new RuntimeException(err);
				}
			}
		} else for (BaseAutoModerationRule rule : checkResult.rules) consumer.accept(rule);
		return checkResult;
	}

	public void reload() throws IOException {
		Path path = this.manager.getAutoModerationRulesPath();
		if (path.toFile().exists()) {
			GsonAutoMod automod = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonAutoMod.class);
			synchronized (this.rules) {
				this.rules.clear();
				for (GsonAutoModerationRule rule : automod) {
					this.rules.add(rule.toAutoModerationRule());
				}
			}
		}
	}

	public void reloadDictionary() throws IOException {
		Path path = this.manager.getAutoModerationDictionaryPath();
		if (path != null && path.toFile().exists()) {
			GsonDictionary dictionary = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonDictionary.class);
			synchronized (this.cleaner) {
				this.cleaner.clear();
				this.cleaner.putAll(dictionary);
			}
		}
	}

	public static class CheckResult {
		private final List<BaseAutoModerationRule> rules;
		private final Map<BaseAutoModerationRule, MatchResult> triggeredRules;
		private final String message;

		private CheckResult(Collection<BaseAutoModerationRule> rules, Map<BaseAutoModerationRule, MatchResult> triggeredRules, String message) {
			this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
			this.triggeredRules = Collections.unmodifiableMap(triggeredRules);
			this.message = message;
		}

		public boolean isTriggered() {
			return !this.triggeredRules.isEmpty();
		}

		public List<BaseAutoModerationRule> getRules() {
			return this.rules;
		}

		public Map<BaseAutoModerationRule, MatchResult> getTriggeredRules() {
			return this.triggeredRules;
		}

		public String getMessage() {
			return this.message;
		}
	}
}