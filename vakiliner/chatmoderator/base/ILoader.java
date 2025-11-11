package vakiliner.chatmoderator.base;

public interface ILoader {
	void saveDefaultConfig();

	void saveConfig();

	void reloadConfig();

	void saveResource(String resourcePath, boolean replace);
}