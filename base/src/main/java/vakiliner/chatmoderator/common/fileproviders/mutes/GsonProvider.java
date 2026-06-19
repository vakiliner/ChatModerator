package vakiliner.chatmoderator.common.fileproviders.mutes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import com.google.gson.Gson;
import vakiliner.chatmoderator.api.GsonMutes;
import vakiliner.chatmoderator.common.FileExtension;
import vakiliner.chatmoderator.common.FileLibrary;

public class GsonProvider implements FileProvider {
	private final Gson gson = new Gson();
	private final Charset encoding;

	public GsonProvider(Charset encoding) {
		this.encoding = Objects.requireNonNull(encoding);
	}

	@Override
	public FileExtension getExtension() {
		return FileExtension.JSON;
	}

	@Override
	public FileLibrary getLibrary() {
		return FileLibrary.GSON;
	}

	@Override
	public void save(Path path, GsonMutes mutes) throws IOException {
		Files.write(path, this.gson.toJson(mutes).getBytes(this.encoding));
	}

	@Override
	public GsonMutes reload(Path path) throws IOException {
		return this.gson.fromJson(new InputStreamReader(Files.newInputStream(path), this.encoding), GsonMutes.class);
	}

	public static GsonlProvider create(Map<String, Object> options) {
		String encodingString = (String) options.get("encoding");
		Charset encoding = encodingString != null ? Charset.forName(encodingString) : StandardCharsets.UTF_8;
		return new GsonlProvider(encoding);
	}
}