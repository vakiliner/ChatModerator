package vakiliner.chatmoderator.bukkit.event;

import java.util.List;
import java.util.NavigableMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import vakiliner.chatmoderator.core.AutoModerationRule;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.AutoModerationRule.Actions;

public class AutoModerationTriggerEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Player sender;
	private final CheckResult checkResult;
	private final Actions actions;
	private boolean cancel = false;

	public AutoModerationTriggerEvent(Player sender, CheckResult checkResult, Actions actions) {
		super(true);
		this.sender = sender;
		this.checkResult = checkResult;
		this.actions = actions;
	}

	public Player getSender() {
		return this.sender;
	}

	public CheckResult getCheckResult() {
		return this.checkResult;
	}

	public Actions getActions() {
		return this.actions;
	}

	public String getMessage() {
		return this.checkResult.getMessage();
	}

	public List<AutoModerationRule> getRules() {
		return this.checkResult.getRules();
	}

	public NavigableMap<AutoModerationRule, AutoModerationRule.MatchResult> getTriggeredRules() {
		return this.checkResult.getTriggeredRules();
	}

	public String getBlockAction() {
		return this.actions.blockAction();
	}

	public int getMuteTime() {
		return this.actions.muteTime();
	}

	public boolean getLogAdmins() {
		return this.actions.logAdmins();
	}

	public boolean isBlockAction() {
		return this.actions.blockAction() != null;
	}

	public String getCustomBlockMessage() {
		String blockAction = this.actions.blockAction();
		return blockAction == null || blockAction.isEmpty() ? null : blockAction;
	}

	public boolean isMutePlayer() {
		return this.actions.muteTime() > 0;
	}

	public void setBlockAction(String blockAction) {
		this.actions.blockAction(blockAction);
	}

	public void setMuteTime(int muteTime) {
		this.actions.muteTime(muteTime);
	}

	public void setLogAdmins(boolean logAdmins) {
		this.actions.logAdmins(logAdmins);
	}

	public boolean isCancelled() {
		return this.cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}