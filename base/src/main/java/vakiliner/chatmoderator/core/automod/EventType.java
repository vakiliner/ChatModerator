package vakiliner.chatmoderator.core.automod;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import com.google.common.collect.Maps;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;

public enum EventType {
	MESSAGE(1, MessageActions::new, MessageActions::new);

	private static final Map<Integer, EventType> BY_INT = Maps.newHashMap();
	private final int type;
	private final Supplier<BaseActions> constructor;
	private final Function<GsonAutoModerationRule.Actions, BaseActions> constructor2;

	private EventType(int type, Supplier<BaseActions> constructor, Function<GsonAutoModerationRule.Actions, BaseActions> constructor2) {
		this.type = type;
		this.constructor = constructor;
		this.constructor2 = constructor2;
	}

	public int asInt() {
		return this.type;
	}

	public final BaseActions createActions() {
		return this.constructor.get();
	}

	public final BaseActions createActions(GsonAutoModerationRule.Actions data) {
		return this.constructor2.apply(data);
	}

	public static EventType getByInt(int type) {
		return BY_INT.get(type);
	}

	static {
		for (EventType eventType : values()) {
			BY_INT.put(eventType.asInt(), eventType);
		}
	}
}