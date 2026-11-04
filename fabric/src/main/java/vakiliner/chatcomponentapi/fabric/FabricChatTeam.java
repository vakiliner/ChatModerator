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

	@Override
	public String getName() {
		return this.team.getName();
	}

	@Override
	public ChatNamedColor getColor() {
		return ChatNamedColor.getByFormat(FabricParser.fabric(this.team.getColor()));
	}

	@Override
	public ChatComponent getDisplayName() {
		return FabricParser.fabric(this.team.getDisplayName());
	}

	@Override
	public ChatComponent getPrefix() {
		return FabricParser.fabric(this.team.getPlayerPrefix());
	}

	@Override
	public ChatComponent getSuffix() {
		return FabricParser.fabric(this.team.getPlayerSuffix());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			FabricChatTeam other = (FabricChatTeam) obj;
			return this.parser.equals(other.parser) && this.team.equals(other.team);
		} else {
			return false;
		}
	}
}
