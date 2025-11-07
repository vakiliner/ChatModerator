package vakiliner.chatmoderator.core.automod;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import com.google.common.collect.Maps;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.core.AutoModeration;

public class TriggerType<Rule extends BaseAutoModerationRule> {
	private static final Map<Integer, TriggerType<?>> BY_INT = Maps.newHashMap();
	public static final TriggerType<KeywordAutoModerationRule> KEYWORD = new TriggerType<>(1, KeywordAutoModerationRule::new);
	private final int type;
	private final BiFunction<AutoModeration, GsonAutoModerationRule, Rule> constructor;

	public TriggerType(int type, BiFunction<AutoModeration, GsonAutoModerationRule, Rule> constructor) {
		this.type = type;
		this.constructor = Objects.requireNonNull(constructor);
		BY_INT.put(this.asInt(), this);
	}

	public final int asInt() {
		return this.type;
	}

	public final Rule createRule(GsonAutoModerationRule data) {
		return this.constructor.apply(ChatModerator.MANAGER.automod, data);
	}

	public static TriggerType<?> getByInt(int type) {
		return BY_INT.get(type);
	}
}