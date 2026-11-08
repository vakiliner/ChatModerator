package vakiliner.chatcomponentapi.craftbukkit;

import java.util.Objects;
import org.bukkit.scoreboard.Team;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;

public class BukkitChatTeam implements ChatTeam {
	protected final BukkitParser parser;
	protected final Team team;

	public BukkitChatTeam(BukkitParser parser, Team team) {
		this.parser = Objects.requireNonNull(parser);
		this.team = Objects.requireNonNull(team);
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
		return new ChatTextComponent(this.team.getDisplayName());
	}

	@Override
	public ChatComponent getPrefix() {
		return new ChatTextComponent(this.team.getPrefix());
	}

	@Override
	public ChatComponent getSuffix() {
		return new ChatTextComponent(this.team.getSuffix());
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
