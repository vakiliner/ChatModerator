package vakiliner.chatcomponentapi.paper;

import org.bukkit.scoreboard.Team;
import net.kyori.adventure.text.format.NamedTextColor;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitChatTeam;

public class PaperChatTeam extends BukkitChatTeam {
	public PaperChatTeam(PaperParser parser, Team team) {
		super(parser, team);
	}

	public ChatComponent getDisplayName() {
		return PaperParser.paper(this.team.displayName());
	}

	public ChatNamedColor getColor() {
		try {
			return PaperParser.paper((NamedTextColor) this.team.color());
		} catch (IllegalStateException err) {
			return ChatNamedColor.RESET;
		}
	}

	public ChatComponent getPrefix() {
		return PaperParser.paper(this.team.prefix());
	}

	public ChatComponent getSuffix() {
		return PaperParser.paper(this.team.suffix());
	}
}