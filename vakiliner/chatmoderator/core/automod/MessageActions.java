package vakiliner.chatmoderator.core.automod;

import vakiliner.chatmoderator.api.GsonAutoModerationRule;

public class MessageActions extends BaseActions {
	private String blockAction;
	private int muteTime;
	private String muteReason;

	public MessageActions() {
	}

	public MessageActions(GsonAutoModerationRule.Actions actions) {
		super(actions);
		if (actions.block_action != null) this.blockAction = actions.block_action;
		if (actions.mute_time != null) this.muteTime = actions.mute_time;
		if (actions.mute_reason != null) this.muteReason = actions.mute_reason;
	}

	public String blockAction() {
		return this.blockAction;
	}

	public int muteTime() {
		return this.muteTime;
	}

	public String muteReason() {
		return this.muteReason;
	}

	public synchronized void blockAction(String message) {
		this.blockAction = message;
	}

	public synchronized void muteTime(int muteTime) {
		if (muteTime < 0) {
			throw new IllegalArgumentException("Invalid mute time: " + muteTime);
		}
		this.muteTime = muteTime;
	}

	public synchronized void muteReason(String muteReason) {
		this.muteReason = muteReason;
	}
}