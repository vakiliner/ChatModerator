package vakiliner.chatcomponentapi.forge;

import java.util.Objects;
import net.minecraft.scoreboard.ScorePlayerTeam;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class ForgeChatTeam implements ChatTeam {
	protected final ForgeParser parser;
	protected final ScorePlayerTeam team;

	public ForgeChatTeam(ForgeParser parser, ScorePlayerTeam team) {
		this.parser = Objects.requireNonNull(parser);
		this.team = Objects.requireNonNull(team);
	}

	public ScorePlayerTeam getTeam() {
		return this.team;
	}

	public String getName() {
		return this.team.getName();
	}

	public ChatNamedColor getColor() {
		return ChatNamedColor.getByFormat(ForgeParser.forge(this.team.getColor()));
	}

	public ChatComponent getDisplayName() {
		return ForgeParser.forge(this.team.getDisplayName());
	}

	public ChatComponent getPrefix() {
		return ForgeParser.forge(this.team.getPlayerPrefix());
	}

	public ChatComponent getSuffix() {
		return ForgeParser.forge(this.team.getPlayerSuffix());
	}
}