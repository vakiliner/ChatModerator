package vakiliner.chatmoderator.core.automod;

import java.util.Map;
import java.util.function.Function;
import com.google.common.collect.Maps;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;

public enum EventType {
	MESSAGE(1, MessageActions::new);

	private static final Map<Integer, EventType> BY_INT = Maps.newHashMap();
	private final int type;
	private final Function<GsonAutoModerationRule.Actions, BaseActions> constructor;

	private EventType(int type, Function<GsonAutoModerationRule.Actions, BaseActions> constructor) {
		this.type = type;
		this.constructor = constructor;
	}

	public int asInt() {
		return this.type;
	}

	public final BaseActions createActions(GsonAutoModerationRule.Actions data) {
		return this.constructor.apply(data);
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