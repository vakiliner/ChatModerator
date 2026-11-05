package vakiliner.chatcomponentapi.craftbukkit;

import static java.util.Objects.requireNonNull;
import org.bukkit.scoreboard.Team;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitChatTeam implements ChatTeam {
	protected final BukkitParser parser;
	protected final Team team;

	public BukkitChatTeam(BukkitParser parser, Team team) {
		this.parser = requireNonNull(parser);
		this.team = requireNonNull(team);
	}

	public Team getTeam() {
		return this.team;
	}

	@Override
	public String getName() {
		return this.team.getName();
	}

	@Override
	public ChatNamedColor getColor() {
		return ChatNamedColor.getByFormat(BukkitParser.bukkit(this.team.getColor()));
	}

	@Override
	public ChatComponent getDisplayName() {
		return this.parser.getDisplayName(this.team);
	}

	@Override
	public ChatComponent getPrefix() {
		return this.parser.getPrefix(this.team);
	}

	@Override
	public ChatComponent getSuffix() {
		return this.parser.getSuffix(this.team);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatTeam other = (BukkitChatTeam) obj;
			return this.parser.equals(other.parser) && this.team.equals(other.team);
		} else {
			return false;
		}
	}
}
