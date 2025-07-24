package vakiliner.chatmoderator.api;

import java.util.List;
import vakiliner.chatmoderator.core.AutoModerationRule;
import vakiliner.chatmoderator.core.AutoModerationRule.TriggerType;

public class GsonAutoModerationRule {
	public String name;
	public Boolean enabled;
	public Integer trigger_type;
	public TriggerMetadata trigger_metadata;
	public Actions actions;

	public static class TriggerMetadata {
		public List<String> keyword_filter;
		public List<String> regex_patterns;
		public List<String> allow_list;
	}

	public static class Actions {
		public Boolean block_action;
		public Integer mute_time;
		public Boolean log_admins;
		public String custom_block_message;
	}

	public AutoModerationRule toAutoModerationRule() {
		AutoModerationRule rule = new AutoModerationRule(this.name, TriggerType.getByInt(this.trigger_type));
		if (this.enabled != null) {
			rule.setEnabled(this.enabled);
		}
		TriggerMetadata metadata = this.trigger_metadata;
		AutoModerationRule.TriggerMetadata metadata2 = rule.getTriggerMetadata();
		if (metadata.keyword_filter != null) metadata2.setKeywordFilter(metadata.keyword_filter);
		if (metadata.regex_patterns != null) metadata2.setRegexPatterns(metadata.regex_patterns);
		if (metadata.allow_list != null) metadata2.setAllowList(metadata.allow_list);
		Actions actions = this.actions;
		AutoModerationRule.Actions actions2 = rule.getActions();
		if (actions.block_action != null) actions2.blockAction(actions.block_action);
		if (actions.mute_time != null) actions2.muteTime(actions.mute_time);
		if (actions.log_admins != null) actions2.logAdmins(actions.log_admins);
		if (actions.custom_block_message != null) actions2.customBlockMessage(actions.custom_block_message);
		return rule;
	}

	public static GsonAutoModerationRule fromAutoModerationRule(AutoModerationRule rule) {
		throw new UnsupportedOperationException();
	}
}