package vakiliner.chatmoderator.core.automod;

import vakiliner.chatmoderator.api.GsonAutoModerationRule;

public class BaseActions {
	protected boolean logAdmins;

	public BaseActions() {
	}

	public BaseActions(GsonAutoModerationRule.Actions actions) {
		if (actions.log_admins != null) this.logAdmins = actions.log_admins;
	}

	public boolean logAdmins() {
		return this.logAdmins;
	}

	public synchronized void logAdmins(boolean logAdmins) {
		this.logAdmins = logAdmins;
	}
}