package vakiliner.chatmoderator.base;

public interface ILoader {
	void saveDefaultConfig();

	void saveResource(String resourcePath, boolean replace);
}