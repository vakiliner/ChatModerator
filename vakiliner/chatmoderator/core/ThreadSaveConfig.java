package vakiliner.chatmoderator.core;

import java.io.IOException;

public class ThreadSaveConfig extends Thread {
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