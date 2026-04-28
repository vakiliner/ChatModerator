package vakiliner.chatmoderator.core.automod;

public class MatchResult {
	private final String keyword;
	private final String content;

	public MatchResult(String keyword, String content) {
		this.keyword = keyword;
		this.content = content;
	}

	public String getKeyword() {
		return this.keyword;
	}

	public String getContent() {
		return this.content;
	}
}