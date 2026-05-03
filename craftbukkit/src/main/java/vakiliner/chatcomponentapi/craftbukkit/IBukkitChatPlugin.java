package vakiliner.chatcomponentapi.craftbukkit;

import org.bukkit.plugin.Plugin;
import vakiliner.chatcomponentapi.base.IChatPlugin;

public interface IBukkitChatPlugin extends IChatPlugin {
	Plugin asPlugin();
}