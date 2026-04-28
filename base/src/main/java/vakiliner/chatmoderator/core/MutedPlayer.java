package vakiliner.chatmoderator.core;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import com.google.common.collect.Maps;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;

public class MutedPlayer {
	private final UUID uuid;
	private final String name;
	private final String moderator;
	private final ModeratorType moderatorType;
	private final Date given;
	private final Integer duration;
	private final String reason;

	public MutedPlayer(ChatOfflinePlayer player, String moderator, ModeratorType moderatorType, Date given, Integer duration, String reason) {
		this(player.getUniqueId(), player.getName(), moderator, moderatorType, given, duration, reason);
	}

	public MutedPlayer(UUID uuid, String name, String moderator, ModeratorType moderatorType, Date given, Integer duration, String reason) {
		this.uuid = Objects.requireNonNull(uuid);
		this.name = Objects.requireNonNull(name);
		this.moderator = Objects.requireNonNull(moderator);
		this.moderatorType = Objects.requireNonNull(moderatorType);
		this.given = Objects.requireNonNull(given);
		this.duration = duration;
		this.reason = reason;
	}

	public UUID getUniqueId() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}

	public String getModeratorName() {
		return this.moderator;
	}

	public ModeratorType getModeratorType() {
		return this.moderatorType;
	}

	public Long getGivenTimestamp() {
		return this.given.getTime();
	}

	public Date getGivenAt() {
		return new Date(this.getGivenTimestamp());
	}

	public Integer getDuration() {
		return this.duration;
	}

	public boolean isInfinite() {
		return this.duration == null;
	}

	public String getReason() {
		return this.reason;
	}

	public long getExpirationTimestamp() {
		Integer duration = this.duration;
		if (duration == null) return -1;
		return this.given.getTime() + this.duration * 1_000;
	}

	public Date getExpirationAt() {
		long timestamp = this.getExpirationTimestamp();
		if (timestamp == -1) return null;
		return new Date(timestamp);
	}

	public boolean isExpired() {
		if (this.isInfinite()) return false;
		return this.isExpired(new Date());
	}

	public boolean isExpired(Date now) {
		if (this.isInfinite()) return false;
		return this.getExpirationAt().before(now);
	}

	public static enum ModeratorType {
		UNKNOWN(0),
		PLAYER(1),
		SERVER(2),
		AUTOMOD(3),
		PLUGIN(4);

		private static final Map<Integer, ModeratorType> BY_INT = Maps.newHashMap();
		private final byte type;

		private ModeratorType(int type) {
			this.type = (byte) type;
		}

		public byte get() {
			return this.type;
		}

		public static ModeratorType valueOf(int type) {
			return BY_INT.get(type);
		}

		static {
			for (ModeratorType moderatorType : values()) {
				BY_INT.put((int) moderatorType.type, moderatorType);
			}
		}
	}
}