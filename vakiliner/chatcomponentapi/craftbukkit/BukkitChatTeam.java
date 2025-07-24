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

	@SuppressWarnings("deprecation")
	public ChatNamedColor getColor() {
		return ChatNamedColor.getByFormat(BukkitParser.bukkit(this.team.getColor()));
	}

	@SuppressWarnings("deprecation")
	public ChatComponent getDisplayName() {
		return new ChatTextComponent(this.team.getDisplayName());
	}

	@SuppressWarnings("deprecation")
	public ChatComponent getPrefix() {
		return new ChatTextComponent(this.team.getPrefix());
	}

	@SuppressWarnings("deprecation")
	public ChatComponent getSuffix() {
		return new ChatTextComponent(this.team.getSuffix());
	}
}