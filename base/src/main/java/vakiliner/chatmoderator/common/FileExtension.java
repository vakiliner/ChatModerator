package vakiliner.chatmoderator.common;

public class FileExtension {
	public static final FileExtension AUTODETECT = new FileExtension("autodetect");
	public static final FileExtension JSON = new FileExtension("json");
	public static final FileExtension JSONL = new FileExtension("jsonl");
	private final String name;

	protected FileExtension(String name) {
		this.name = name;
	}

	public String getFilename(String filename) {
		return filename + '.' + name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}