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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.google.gson.Gson;
import vakiliner.chatmoderator.api.GsonAutoMod;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;
import vakiliner.chatmoderator.api.GsonDictionary;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.core.AutoModerationRule.Actions;
import vakiliner.chatmoderator.core.AutoModerationRule.TriggerType;

public class AutoModeration {
	private final ChatModerator manager;
	private final Map<Character, String> cleaner = new HashMap<>();
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	private final List<AutoModerationRule> rules = new ArrayList<>();

	public AutoModeration(ChatModerator manager) {
		this.manager = manager;
	}

	public CheckResult checkMessage(String message, TriggerType triggerType) {
		Objects.requireNonNull(triggerType);
		ArrayList<AutoModerationRule> rules = new ArrayList<>();
		LinkedList<AutoModerationRule> triggeredRules = new LinkedList<>();
		for (AutoModerationRule rule : this.rules) {
			if (rule.isEnabled() && rule.getTriggerType() == triggerType) {
				rules.add(rule);
			}
		}
		CheckResult checkResult = new CheckResult(rules, triggeredRules);
		rules.sort((a, b) -> b.getActions().muteTime() - a.getActions().muteTime());
		for (AutoModerationRule rule : rules) {
			Object result = rule.checkText(message, cleaner);
			Actions actions = rule.getActions();
			int muteTime = actions.muteTime();
			boolean mute = muteTime > 0;
			trigger: if (result != null) {
				if (mute) {
					AutoModerationRule firstRule = triggeredRules.peek();
					if (firstRule != null && muteTime - firstRule.getActions().muteTime() > 0) {
						triggeredRules.addFirst(rule);
						break trigger;
					}
				}
				triggeredRules.addLast(rule);
			}
		}
		return checkResult.parse();
	}

	public CheckResult checkMessageInThreadPool(String message, TriggerType triggerType) throws InterruptedException {
		Objects.requireNonNull(triggerType);
		ArrayList<AutoModerationRule> rules = new ArrayList<>();
		LinkedList<AutoModerationRule> triggeredRules = new LinkedList<>();
		for (AutoModerationRule rule : this.rules) {
			if (rule.isEnabled() && rule.getTriggerType() == triggerType) {
				rules.add(rule);
			}
		}
		CheckResult checkResult = new CheckResult(rules, triggeredRules);
		rules.sort((a, b) -> b.getActions().muteTime() - a.getActions().muteTime());
		List<Future<?>> waitlist = new ArrayList<>();
		for (AutoModerationRule rule : rules) waitlist.add(this.threadPool.submit(() -> {
			Object result = rule.checkText(message, cleaner);
			Actions actions = rule.getActions();
			int muteTime = actions.muteTime();
			boolean mute = muteTime > 0;
			trigger: if (result != null) {
				if (mute) {
					AutoModerationRule firstRule = triggeredRules.peek();
					if (firstRule != null && muteTime - firstRule.getActions().muteTime() > 0) {
						triggeredRules.addFirst(rule);
						break trigger;
					}
				}
				triggeredRules.addLast(rule);
			}
		}));
		for (Future<?> future : waitlist) {
			try {
				future.get();
			} catch (ExecutionException err) {
				throw new RuntimeException(err);
			}
		}
		return checkResult.parse();
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
		private final List<AutoModerationRule> triggeredRules;
		private AutoModerationRule blockAction;
		private AutoModerationRule muteTime;
		private AutoModerationRule logAdmins;

		private CheckResult(Collection<AutoModerationRule> rules, List<AutoModerationRule> triggeredRules) {
			this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
			this.triggeredRules = Collections.unmodifiableList(triggeredRules);
		}

		public boolean isTriggered() {
			return !this.triggeredRules.isEmpty();
		}

		public List<AutoModerationRule> getRules() {
			return this.rules;
		}

		public List<AutoModerationRule> getTriggeredRules() {
			return this.triggeredRules;
		}

		public AutoModerationRule blockAction() {
			return this.blockAction;
		}

		public AutoModerationRule muteTime() {
			return this.muteTime;
		}

		public AutoModerationRule logAdmins() {
			return this.logAdmins;
		}

		private CheckResult parse() {
			for (AutoModerationRule rule : this.triggeredRules) {
				Actions actions = rule.getActions();
				if (actions.blockAction() && this.blockAction == null) {
					this.blockAction = rule;
				}
				if (actions.mutePlayer() && this.muteTime == null) {
					this.muteTime = rule;
				}
				if (actions.logAdmins() && this.logAdmins == null) {
					this.logAdmins = rule;
				}
			}
			return this;
		}
	}
}