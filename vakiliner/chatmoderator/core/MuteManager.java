package vakiliner.chatmoderator.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import vakiliner.chatmoderator.base.ChatModerator;
import vakiliner.chatmoderator.core.MutedPlayer.ModeratorType;

public class MuteManager {
	private final ChatModerator manager;
	private final Map<UUID, MutedPlayer> map = new HashMap<>();
	private final Map<String, MutedPlayer> byName = new HashMap<>();
	private final ThreadSaveConfig threadSaveConfig = new ThreadSaveConfig(this);

	public MuteManager(ChatModerator manager) {
		this.manager = manager;
		this.threadSaveConfig.start();
	}

	public MutedPlayer get(UUID uuid) {
		return this.map.get(uuid);
	}

	public MutedPlayer getByName(String name) {
		return this.byName.get(name.toLowerCase());
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
		if (put == null) this.byName.putIfAbsent(mute.getName().toLowerCase(), mute);
		return put == null;
	}

	private synchronized MutedPlayer remove(UUID uuid) {
		MutedPlayer mute = this.map.remove(uuid);
		if (mute != null) this.byName.remove(mute.getName().toLowerCase(), mute);
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

	public void reload() throws IOException {
		Path path = this.manager.getMutesPath();
		if (path.toFile().exists()) {
			GsonMutes mutes = new Gson().fromJson(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8), GsonMutes.class);
			synchronized (this) {
				this.map.clear();
				for (GsonMutedPlayer mute : mutes) {
					this.put(mute.toMutedPlayer());
				}
			}
		}
	}

	public void save() throws IOException {
		Path path = this.manager.getMutesPath();
		GsonMutes mutes = new GsonMutes();
		synchronized (this) {
			for (MutedPlayer mute : this.map.values()) {
				mutes.add(GsonMutedPlayer.fromMutedPlayer(mute));
			}
		}
		Files.write(path, new Gson().toJson(mutes).getBytes(StandardCharsets.UTF_8));
	}
}

class ThreadSaveConfig extends Thread {
	private final MuteManager mute;
	private boolean save;

	public ThreadSaveConfig(MuteManager mute) {
		this.mute = mute;
	}

	public void run() {
		try {
			while (true) {
				synchronized (this) {
					if (!this.save) this.wait();
					this.save = false;
				}
				try {
					this.mute.save();
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