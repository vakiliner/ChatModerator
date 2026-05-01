package vakiliner.chatmoderator.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.api.GsonMutedPlayer;
import vakiliner.chatmoderator.api.GsonMutes;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public class MuteManager {
	private final Map<UUID, MutedPlayer> map = new HashMap<>();
	private ThreadSaveConfig threadSaveConfig;
	public Path filepath;

	public MutedPlayer get(UUID uuid) {
		return this.map.get(uuid);
	}

	public MutedPlayer getMutedPlayer(String name) {
		for (MutedPlayer mute : this.map.values()) {
			if (mute.getName().equalsIgnoreCase(name)) return mute;
		}
		return null;
	}

	public MutedPlayer getMutedPlayerExact(String name) {
		for (MutedPlayer mute : this.map.values()) {
			if (mute.getName().equals(name)) return mute;
		}
		return null;
	}

	public Map<UUID, MutedPlayer> map() {
		return Collections.unmodifiableMap(this.map);
	}

	public boolean mute(ChatOfflinePlayer player, String moderator, ModeratorType moderatorType, Integer duration, String reason) {
		Date now = new Date();
		synchronized (this.map) {
			MutedPlayer mute = this.map.get(player.getUniqueId());
			if (mute != null && !mute.isExpired(now)) return false;
			this.map.put(player.getUniqueId(), new MutedPlayer(player, moderator, moderatorType, now, duration, reason));
		}
		this.threadSaveConfig.save();
		return true;
	}

	@Deprecated
	public boolean unmute(String name) {
		MutedPlayer mute = this.getMutedPlayer(name);
		if (mute != null) {
			return this.unmute(mute.getUniqueId());
		}
		return false;
	}

	public boolean unmute(UUID uuid) {
		MutedPlayer mute;
		synchronized (this.map) {
			mute = this.map.remove(uuid);
		}
		if (mute != null) this.threadSaveConfig.save();
		return mute != null && !mute.isExpired();
	}

	public synchronized void setup(Path path) throws IOException {
		if (this.threadSaveConfig != null) {
			throw new IllegalStateException("Thread not stopped");
		}
		this.threadSaveConfig = new ThreadSaveConfig(this);
		this.threadSaveConfig.start();
		this.reload(this.filepath = path, true);
	}

	public synchronized void stop() throws IOException {
		this.threadSaveConfig.interrupt();
		this.threadSaveConfig = null;
		this.save();
		this.filepath = null;
		this.map.clear();
	}

	public void reload() throws IOException {
		this.reload(false);
	}

	public void reload(File file) throws IOException {
		this.reload(file, false);
	}

	public void reload(Path path) throws IOException {
		this.reload(path, false);
	}

	public void reload(boolean saveIfNotExists) throws IOException {
		this.reload(this.filepath, saveIfNotExists);
	}

	public void reload(File file, boolean saveIfNotExists) throws IOException {
		this.reload(file, file.toPath(), saveIfNotExists);
	}

	public void reload(Path path, boolean saveIfNotExists) throws IOException {
		this.reload(path.toFile(), path, saveIfNotExists);
	}

	private void reload(File file, Path path, boolean saveIfNotExists) throws IOException {
		if (file.exists()) {
			GsonMutes mutes = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonMutes.class);
			synchronized (this.map) {
				this.map.clear();
				for (GsonMutedPlayer mute : mutes) {
					this.map.put(mute.uuid, mute.toMutedPlayer());
				}
			}
		} else if (!file.getParentFile().exists()) {
			throw new NoSuchFileException(file.toString());
		} else if (saveIfNotExists) {
			this.save(path);
		}
	}

	public void save() throws IOException {
		this.save(this.filepath);
	}

	public void save(File file) throws IOException {
		this.save(file.toPath());
	}

	public void save(Path path) throws IOException {
		GsonMutes mutes = new GsonMutes();
		synchronized (this.map) {
			for (MutedPlayer mute : this.map.values()) {
				mutes.add(GsonMutedPlayer.fromMutedPlayer(mute));
			}
		}
		Files.write(path, new Gson().toJson(mutes).getBytes(StandardCharsets.UTF_8));
	}
}

class ThreadSaveConfig extends Thread {
	private final MuteManager manager;
	private boolean save;

	public ThreadSaveConfig(MuteManager manager) {
		this.manager = manager;
	}

	public void run() {
		try {
			while (true) {
				synchronized (this) {
					if (!this.save) this.wait();
					this.save = false;
				}
				try {
					this.manager.save();
				} catch (IOException err) {
					err.printStackTrace();
				}
				Thread.sleep(15_000);
			}
		} catch (InterruptedException err) {
			this.interrupt();
		}
	}

	public synchronized void save() {
		this.save = true;
		this.notify();
	}
}