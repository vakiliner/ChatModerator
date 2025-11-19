package vakiliner.chatmoderator.api;

import com.google.gson.JsonObject;
import vakiliner.chatmoderator.core.automod.BaseAutoModerationRule;
import vakiliner.chatmoderator.core.automod.TriggerType;

public class GsonAutoModerationRule {
	public String name;
	public Boolean enabled;
	public Integer event_type;
	public Integer trigger_type;
	public JsonObject trigger_metadata;
	public Actions actions;

	public static class Actions {
		public String block_action;
		public Integer mute_time;
		public String mute_reason;
		public Boolean log_admins;
	}

	public BaseAutoModerationRule toAutoModerationRule() {
		return TriggerType.getByInt(this.trigger_type).createRule(this);
	}

	public static GsonAutoModerationRule fromAutoModerationRule(BaseAutoModerationRule rule) {
		throw new UnsupportedOperationException();
	}
}