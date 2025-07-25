package vakiliner.chatmoderator.bukkit;

import java.io.IOException;
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

	public void onEnable() {
		MANAGER.init(this);
		this.saveDefaultConfig();
		if (!MANAGER.getAutoModerationRulesPath().toFile().exists()) {
			this.saveResource("auto_moderation_rules.json", false);
		}
		this.reloadConfig();
		String dictionaryFile = MANAGER.config.dictionaryFile();
		if (dictionaryFile != null && dictionaryFile.equals("dictionary_ru.json")) {
			if (!MANAGER.getAutoModerationDictionaryPath().toFile().exists()) {
				this.saveResource("dictionary_ru.json", false);
			}
		}
		try {
			MANAGER.automod.reload();
		} catch (IOException err) {
			err.printStackTrace();
		}
		Bukkit.getConsoleSender().addAttachment(this, "chatmoderator.*", true);
		Bukkit.getPluginManager().registerEvents(this.listener, this);
		this.getCommand("mute").setExecutor(new MuteCommand(MANAGER));
		this.getCommand("unmute").setExecutor(new UnmuteCommand(MANAGER));
		this.getCommand("mutes").setExecutor(new MuteListCommand(MANAGER));
		this.getLogger().info("Плагин запущен " + MANAGER.getClass().getSimpleName());
	}

	public void onDisable() {
		try {
			MANAGER.mutes.save();
		} catch (IOException err) {
			err.printStackTrace();
		}
		this.getLogger().info("Плагин выключен");
	}

	public void reloadConfig() {
		super.reloadConfig();
		MANAGER.config.reload(this.getConfig());
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Collections.emptyList();
	}
}