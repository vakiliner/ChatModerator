package vakiliner.chatcomponentapi.common;

import java.util.Map;
import com.google.common.collect.Maps;

public enum ChatGameMode {
	CREATIVE(1),
	SURVIVAL(0),
	ADVENTURE(2),
	SPECTATOR(3);

	private static final Map<Integer, ChatGameMode> BY_ID = Maps.newHashMap();
	private final int value;

	private ChatGameMode(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static ChatGameMode getByValue(int value) {
		return BY_ID.get(value);
	}

	static {
		for (ChatGameMode gameMode : values()) {
			BY_ID.put(gameMode.value, gameMode);
		}
	}
}