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
import java.util.NavigableMap;
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
import vakiliner.chatmoderator.core.AutoModerationRule.MatchResult;
import vakiliner.chatmoderator.core.AutoModerationRule.TriggerType;

public class AutoModeration {
	private final ChatModerator manager;
	private final Map<Character, String> cleaner = new HashMap<>();
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	private final List<AutoModerationRule> rules = new ArrayList<>();

	public AutoModeration(ChatModerator manager) {
		this.manager = manager;
	}

	public CheckResult checkMessage(String message) {
		try {
			return this.check(message, TriggerType.MESSAGE, false);
		} catch (InterruptedException err) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(err);
		}
	}

	public CheckResult checkMessageInThreadPool(String message) throws InterruptedException {
		return this.check(message, TriggerType.MESSAGE, true);
	}

	private CheckResult check(String message, TriggerType triggerType, boolean useThreadPool) throws InterruptedException {
		Objects.requireNonNull(triggerType);
		ArrayList<AutoModerationRule> rules = new ArrayList<>();
		TreeMap<AutoModerationRule, MatchResult> triggeredRules = new TreeMap<>((a, b) -> b.getActions().muteTime() - a.getActions().muteTime());
		for (AutoModerationRule rule : this.rules) {
			if (rule.isEnabled() && rule.getTriggerType() == triggerType) rules.add(rule);
		}
		CheckResult checkResult = new CheckResult(rules, triggeredRules, message);
		Consumer<AutoModerationRule> consumer = (rule) -> {
			MatchResult matchResult = rule.checkText(checkResult.getMessage(), cleaner);
			if (matchResult != null) synchronized (triggeredRules) {
				triggeredRules.put(rule, matchResult);
			}
		};
		int size = checkResult.rules.size();
		if (useThreadPool) {
			List<Future<?>> waitlist = new ArrayList<>();
			int index = 0;
			for (AutoModerationRule rule : checkResult.rules) {
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
		} else for (AutoModerationRule rule : checkResult.rules) consumer.accept(rule);
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
		Path dictionaryPath = this.manager.getAutoModerationDictionaryPath();
		if (dictionaryPath != null && dictionaryPath.toFile().exists()) {
			GsonDictionary dictionary = new Gson().fromJson(new InputStreamReader(Files.newInputStream(dictionaryPath), StandardCharsets.UTF_8), GsonDictionary.class);
			synchronized (this.cleaner) {
				this.cleaner.clear();
				this.cleaner.putAll(dictionary);
			}
		}
	}

	public static class CheckResult {
		private final List<AutoModerationRule> rules;
		private final NavigableMap<AutoModerationRule, MatchResult> triggeredRules;
		private final String message;

		private CheckResult(Collection<AutoModerationRule> rules, NavigableMap<AutoModerationRule, MatchResult> triggeredRules, String message) {
			this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
			this.triggeredRules = Collections.unmodifiableNavigableMap(triggeredRules);
			this.message = message;
		}

		public boolean isTriggered() {
			return !this.triggeredRules.isEmpty();
		}

		public List<AutoModerationRule> getRules() {
			return this.rules;
		}

		public NavigableMap<AutoModerationRule, AutoModerationRule.MatchResult> getTriggeredRules() {
			return this.triggeredRules;
		}

		public String getMessage() {
			return this.message;
		}
	}
}