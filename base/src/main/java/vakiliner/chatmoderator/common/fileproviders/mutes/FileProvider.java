package vakiliner.chatmoderator.common.fileproviders.mutes;

import java.io.IOException;
import java.nio.file.Path;
import vakiliner.chatmoderator.api.GsonMutes;
import vakiliner.chatmoderator.common.FileExtension;
import vakiliner.chatmoderator.common.FileLibrary;

public interface FileProvider {
	FileExtension getExtension();

	FileLibrary getLibrary();

	void save(Path path, GsonMutes mutes) throws IOException;

	GsonMutes reload(Path path) throws IOException;
}