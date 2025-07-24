package vakiliner.chatcomponentapi.fabric;

import java.util.Objects;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import vakiliner.chatcomponentapi.base.ChatTeam;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class FabricChatTeam implements ChatTeam {
	protected final FabricParser parser;
	protected final PlayerTeam team;

	public FabricChatTeam(FabricParser parser, PlayerTeam team) {
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
		return ChatNamedColor.getByFormat(FabricParser.fabric(this.team.getColor()));
	}

	public ChatComponent getDisplayName() {
		return FabricParser.fabric(this.team.getDisplayName());
	}

	public ChatComponent getPrefix() {
		return FabricParser.fabric(this.team.getPlayerPrefix());
	}

	public ChatComponent getSuffix() {
		return FabricParser.fabric(this.team.getPlayerSuffix());
	}
}