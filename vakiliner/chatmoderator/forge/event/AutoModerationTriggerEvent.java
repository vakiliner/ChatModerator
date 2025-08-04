package vakiliner.chatmoderator.forge.event;

import java.util.List;
import java.util.NavigableMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.AutoModerationRule;
import vakiliner.chatmoderator.core.AutoModerationRule.Actions;

@Cancelable
public class AutoModerationTriggerEvent extends Event {
	private final ServerPlayerEntity sender;
	private final CheckResult checkResult;
	private final Actions actions;

	public AutoModerationTriggerEvent(ServerPlayerEntity sender, CheckResult checkResult, Actions actions) {
		this.sender = sender;
		this.checkResult = checkResult;
		this.actions = actions;
	}

	public ServerPlayerEntity getSender() {
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
}