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

	public String getName() {
		return this.team.getName();
	}

	public ChatNamedColor getColor() {
		return ChatNamedColor.getByFormat(BukkitParser.bukkit(this.team.getColor()));
	}

	public ChatComponent getDisplayName() {
		return new ChatTextComponent(this.team.getDisplayName());
	}

	public ChatComponent getPrefix() {
		return new ChatTextComponent(this.team.getPrefix());
	}

	public ChatComponent getSuffix() {
		return new ChatTextComponent(this.team.getSuffix());
	}
}