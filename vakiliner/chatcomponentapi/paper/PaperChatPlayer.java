package vakiliner.chatcomponentapi.paper;

import org.bukkit.entity.Player;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.craftbukkit.BukkitChatPlayer;

public class PaperChatPlayer extends BukkitChatPlayer {
	public PaperChatPlayer(PaperParser parser, Player player) {
		super(parser, player);
	}

	public ChatComponent getDisplayName() {
		return PaperParser.paper(this.getPlayer().displayName());
	}
}