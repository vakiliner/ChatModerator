package vakiliner.chatcomponentapi.forge;

import static java.util.Objects.requireNonNull;
import net.minecraft.scoreboard.ScorePlayerTeam;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatTeam implements ChatTeam {
	protected final ForgeParser parser;
	protected final ScorePlayerTeam team;

	public ForgeChatTeam(ForgeParser parser, ScorePlayerTeam team) {
		this.parser = requireNonNull(parser);
		this.team = requireNonNull(team);
	}

	public ScorePlayerTeam getTeam() {
		return this.team;
	}

	@Override
	public String getName() {
		return this.team.getName();
	}

	@Override
	public ChatNamedColor getColor() {
		return ChatNamedColor.getByFormat(ForgeParser.forge(this.team.getColor()));
	}

	@Override
	public ChatComponent getDisplayName() {
		return ForgeParser.forge(this.team.getDisplayName());
	}

	@Override
	public ChatComponent getPrefix() {
		return ForgeParser.forge(this.team.getPlayerPrefix());
	}

	@Override
	public ChatComponent getSuffix() {
		return ForgeParser.forge(this.team.getPlayerSuffix());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ForgeChatTeam other = (ForgeChatTeam) obj;
			return this.parser.equals(other.parser) && this.team.equals(other.team);
		} else {
			return false;
		}
	}
}
