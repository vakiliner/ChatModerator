package vakiliner.chatmoderator.common;

public class FileLibrary {
	public static final FileLibrary RECOMMENDED = new FileLibrary("recommended");
	public static final FileLibrary GSON = new FileLibrary("gson");
	private final String name;

	protected FileLibrary(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}