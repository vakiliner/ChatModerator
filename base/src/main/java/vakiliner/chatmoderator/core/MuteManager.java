package vakiliner.chatmoderator.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import com.google.gson.Gson;
import vakiliner.chatcomponentapi.base.ChatOfflinePlayer;
import vakiliner.chatmoderator.api.GsonMutedPlayer;
import vakiliner.chatmoderator.api.GsonMutes;
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public class MuteManager {
	@SuppressWarnings("unused")
	private final ChatModerator manager;
	private final Map<UUID, MutedPlayer> map = new HashMap<>();
	private final Map<String, Set<UUID>> byName = new HashMap<>();
	private ThreadSaveConfig threadSaveConfig;
	private File file;

	public MuteManager(ChatModerator manager) {
		this.manager = manager;
	}

	public MutedPlayer get(UUID uuid) {
		return this.map.get(uuid);
	}

	public MutedPlayer getByName(String name) {
		Set<UUID> set = this.byName.get(name.toLowerCase());
		UUID uuid;
		if (set != null) try {
			uuid = set.iterator().next();
		} catch (NoSuchElementException err) {
			uuid = null;
		} else {
			uuid = null;
		}
		return uuid != null ? this.get(uuid) : null;
	}

	@Deprecated
	public MutedPlayer getMutedPlayer(String name) {
		for (MutedPlayer mute : this.map.values()) {
			if (mute.getName().equalsIgnoreCase(name)) return mute;
		}
		return null;
	}

	@Deprecated
	public MutedPlayer getMutedPlayerExact(String name) {
		for (MutedPlayer mute : this.map.values()) {
			if (mute.getName().equals(name)) return mute;
		}
		return null;
	}

	public Map<UUID, MutedPlayer> map() {
		return Collections.unmodifiableMap(this.map);
	}

	private synchronized boolean put(MutedPlayer mute) {
		MutedPlayer put = this.map.putIfAbsent(mute.getUniqueId(), mute);
		if (put == null) {
			String key = mute.getName().toLowerCase();
			Set<UUID> set = this.byName.get(key);
			if (set == null) {
				this.byName.put(key, set = new HashSet<>());
			}
			set.add(mute.getUniqueId());
		}
		return put == null;
	}

	private synchronized MutedPlayer remove(UUID uuid) {
		MutedPlayer mute = this.map.remove(uuid);
		if (mute != null) {
			String key = mute.getName().toLowerCase();
			Set<UUID> set = this.byName.get(key);
			if (set != null) {
				set.remove(mute.getUniqueId());
				if (set.isEmpty()) {
					this.byName.remove(key, mute);
				}
			}
		};
		return mute;
	}

	public boolean mute(ChatOfflinePlayer player, String moderator, ModeratorType moderatorType, Integer duration, String reason) {
		Date now = new Date();
		synchronized (this) {
			MutedPlayer mute = this.map.get(player.getUniqueId());
			if (mute != null && !mute.isExpired(now)) return false;
			if (!this.put(new MutedPlayer(player, moderator, moderatorType, now, duration, reason))) return false;
		}
		this.threadSaveConfig.save();
		return true;
	}

	@Deprecated
	public boolean unmute(String name) {
		MutedPlayer mute = this.getByName(name);
		return mute != null && this.unmute(mute.getUniqueId());
	}

	public boolean unmute(UUID uuid) {
		MutedPlayer mute = this.remove(uuid);
		if (mute != null) this.threadSaveConfig.save();
		return mute != null && !mute.isExpired();
	}

	public synchronized void setup(File file) throws IOException {
		if (this.threadSaveConfig != null) {
			throw new IllegalStateException("Thread not stopped");
		}
		this.threadSaveConfig = new ThreadSaveConfig(this);
		this.threadSaveConfig.start();
		this.reload(this.file = file);
	}

	public synchronized void stop() throws IOException {
		this.threadSaveConfig.interrupt();
		this.threadSaveConfig = null;
		this.save();
		this.file = null;
		this.map.clear();
	}

	public void reload() throws IOException {
		this.reload(this.file);
	}

	public void reload(File file) throws IOException {
		if (file.exists()) {
			GsonMutes mutes = new Gson().fromJson(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8), GsonMutes.class);
			synchronized (this) {
				this.map.clear();
				for (GsonMutedPlayer mute : mutes) {
					this.put(mute.toMutedPlayer());
				}
			}
		}
	}

	public void save() throws IOException {
		this.save(this.file);
	}

	public void save(File file) throws IOException {
		GsonMutes mutes = new GsonMutes();
		synchronized (this) {
			for (MutedPlayer mute : this.map.values()) {
				mutes.add(GsonMutedPlayer.fromMutedPlayer(mute));
			}
		}
		Files.write(file.toPath(), new Gson().toJson(mutes).getBytes(StandardCharsets.UTF_8));
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