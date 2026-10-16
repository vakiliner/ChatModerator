package vakiliner.chatmoderator.common.fileproviders.mutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import com.google.gson.Gson;
import vakiliner.chatmoderator.api.GsonMutedPlayer;
import vakiliner.chatmoderator.api.GsonMutes;
import vakiliner.chatmoderator.common.FileExtension;
import vakiliner.chatmoderator.common.FileLibrary;

public class GsonlProvider implements FileProvider {
	private final Gson gson = new Gson();
	private final Charset encoding;

	public GsonlProvider(Charset encoding) {
		this.encoding = Objects.requireNonNull(encoding);
	}

	@Override
	public FileExtension getExtension() {
		return FileExtension.JSONL;
	}

	@Override
	public FileLibrary getLibrary() {
		return FileLibrary.GSON;
	}

	@Override
	public void save(Path path, GsonMutes mutes) throws IOException {
		try (OutputStream stream = Files.newOutputStream(path)) {
			for (GsonMutedPlayer mute : mutes) {
				byte[] bytes = this.gson.toJson(mute).getBytes(this.encoding);
				stream.write(bytes);
				stream.write('\n');
			}
		}
	}

	@Override
	public GsonMutes reload(Path path) throws IOException {
		GsonMutes mutes = new GsonMutes();
		try (BufferedReader reader = Files.newBufferedReader(path, this.encoding)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if ((line = line.trim()).isEmpty()) continue;
				GsonMutedPlayer mute = this.gson.fromJson(line, GsonMutedPlayer.class);
				mutes.add(mute);
			}
		}
		return mutes;
	}

	public static GsonlProvider create(Map<String, Object> options) {
		String encodingString = (String) options.get("encoding");
		Charset encoding = encodingString != null ? Charset.forName(encodingString) : StandardCharsets.UTF_8;
		return new GsonlProvider(encoding);
	}
}