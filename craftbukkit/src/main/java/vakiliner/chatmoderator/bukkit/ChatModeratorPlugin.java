package vakiliner.chatmoderator.bukkit;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import vakiliner.chatmoderator.bukkit.command.*;
import vakiliner.chatmoderator.paper.PaperChatModerator;
import vakiliner.chatmoderator.spigot.SpigotChatModerator;

public class ChatModeratorPlugin extends JavaPlugin {
	private static final BukkitChatModerator MANAGER;
	private final BukkitListener listener = MANAGER.createListener();

	static {
		BukkitChatModerator manager;
		try {
			Class.forName("net.kyori.adventure.text.Component");
			manager = new PaperChatModerator();
		} catch (ClassNotFoundException paper) {
			try {
				Class.forName("net.md_5.bungee.api.chat.BaseComponent");
				manager = new SpigotChatModerator();
			} catch (ClassNotFoundException spigot) {
				manager = new BukkitChatModerator();
			}
		}
		MANAGER = manager;
	}

	public void onLoad() {
		this.saveDefaultConfig();
	}

	public void onEnable() {
		MANAGER.init(this);
		Bukkit.getPluginManager().registerEvents(this.listener, this);
		this.getCommand("mute").setExecutor(new MuteCommand(MANAGER));
		this.getCommand("unmute").setExecutor(new UnmuteCommand(MANAGER));
		this.getCommand("mutes").setExecutor(new MuteListCommand(MANAGER));
		this.getLogger().info("Plugin enabled");
	}

	public void onDisable() {
		MANAGER.stop(this);
		this.getLogger().info("Plugin disabled");
	}

	public void reloadConfig() {
		super.reloadConfig();
		MANAGER.reloadConfig(this.getConfig());
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Collections.emptyList();
	}
}