package vakiliner.chatmoderator.api;

import java.util.Date;
import java.util.UUID;
import vakiliner.chatmoderator.core.MutedPlayer;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public class GsonMutedPlayer {
	public UUID uuid;
	public String name;
	public String moderator;
	public Integer moderatorType;
	public Date given;
	public Integer duration;
	public String reason;

	public MutedPlayer toMutedPlayer() {
		final ModeratorType moderatorType;
		if (this.moderatorType == null) {
			moderatorType = ModeratorType.UNKNOWN;
		} else {
			moderatorType = ModeratorType.valueOf(this.moderatorType);
		}
		return new MutedPlayer(this.uuid, this.name, this.moderator, moderatorType, this.given, this.duration, this.reason);
	}

	public static GsonMutedPlayer fromMutedPlayer(MutedPlayer mute) {
		GsonMutedPlayer mutedPlayer = new GsonMutedPlayer();
		mutedPlayer.uuid = mute.getUniqueId();
		mutedPlayer.name = mute.getName();
		mutedPlayer.moderator = mute.getModeratorName();
		mutedPlayer.moderatorType = (int) mute.getModeratorType().get();
		mutedPlayer.given = mute.getGivenAt();
		mutedPlayer.duration = mute.getDuration();
		mutedPlayer.reason = mute.getReason();
		return mutedPlayer;
	}
}