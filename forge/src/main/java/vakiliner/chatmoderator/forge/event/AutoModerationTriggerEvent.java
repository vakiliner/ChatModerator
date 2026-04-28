package vakiliner.chatmoderator.forge.event;

import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.automod.BaseAutoModerationRule;
import vakiliner.chatmoderator.core.automod.MatchResult;
import vakiliner.chatmoderator.core.automod.MessageActions;

@Cancelable
public class AutoModerationTriggerEvent extends Event {
	private final ServerPlayerEntity sender;
	private final CheckResult checkResult;
	private final MessageActions actions;

	public AutoModerationTriggerEvent(ServerPlayerEntity sender, CheckResult checkResult, MessageActions actions) {
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

	public MessageActions getActions() {
		return this.actions;
	}

	public String getMessage() {
		return this.checkResult.getMessage();
	}

	public List<BaseAutoModerationRule> getRules() {
		return this.checkResult.getRules();
	}

	public Map<BaseAutoModerationRule, MatchResult> getTriggeredRules() {
		return this.checkResult.getTriggeredRules();
	}

	public String getBlockAction() {
		return this.actions.blockAction();
	}

	public int getMuteTime() {
		return this.actions.muteTime();
	}

	public String getMuteReason() {
		return this.actions.muteReason();
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

	public void setMuteReason(String muteReason) {
		this.actions.muteReason(muteReason);
	}

	public void setLogAdmins(boolean logAdmins) {
		this.actions.logAdmins(logAdmins);
	}
}