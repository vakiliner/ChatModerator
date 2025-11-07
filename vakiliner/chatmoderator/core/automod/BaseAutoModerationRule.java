package vakiliner.chatmoderator.core.automod;

import java.util.Objects;
import java.util.Optional;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.AutoModeration;

public abstract class BaseAutoModerationRule {
	protected final AutoModeration automod;
	protected String name;
	protected boolean enabled = false;
	protected final EventType eventType;
	protected final BaseActions actions;

	public BaseAutoModerationRule(AutoModeration automod, String name, EventType eventType) {
		if (!this.isSupportEventType(this.eventType = Objects.requireNonNull(eventType))) {
			throw new IllegalArgumentException("Event type not support");
		}
		this.automod = automod;
		this.name = Objects.requireNonNull(name);
		this.actions = this.eventType.createActions(null);
	}

	public BaseAutoModerationRule(AutoModeration automod, GsonAutoModerationRule rule) {
		if (!this.isSupportEventType(this.eventType = Objects.requireNonNull(EventType.getByInt(rule.event_type)))) {
			throw new IllegalArgumentException("Event type not support");
		}
		this.automod = automod;
		this.name = Objects.requireNonNull(rule.name);
		this.enabled = Optional.ofNullable(rule.enabled).orElse(false);
		this.actions = this.eventType.createActions(rule.actions);
	}

	public String getName() {
		return this.name;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	protected abstract boolean isSupportEventType(EventType eventType);

	public abstract TriggerType<?> getTriggerType();

	public boolean isEnabled() {
		return this.enabled;
	}

	public BaseActions getActions() {
		return this.actions;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public abstract MatchResult checkText(ChatPlayer player, String rawMessage);
}