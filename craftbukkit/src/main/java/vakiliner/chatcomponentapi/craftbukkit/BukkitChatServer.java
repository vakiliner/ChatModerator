package vakiliner.chatcomponentapi.craftbukkit;

import static java.util.Objects.requireNonNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import com.google.common.collect.Collections2;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.base.ChatPlayerList;
import vakiliner.chatcomponentapi.base.ChatServer;
import vakiliner.chatcomponentapi.base.IChatPlugin;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatComponent;

public class BukkitChatServer implements ChatServer, ChatPlayerList {
	protected static final Class<?> NMS_CLASS;
	private static final Method GET_HANDLE;
	private static final Method EXECUTE_SYNC;
	protected final BukkitParser parser;
	protected final Server server;

	static {
		Server server = Bukkit.getServer();
		try {
			// Gets a method of the CraftServer class, not the Server class
			GET_HANDLE = server.getClass().getMethod("getServer");
			if (!Executor.class.isAssignableFrom(GET_HANDLE.getReturnType())) {
				throw new NoSuchMethodException();
			}
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
		NMS_CLASS = getNMS(server).getClass();
		try {
			EXECUTE_SYNC = NMS_CLASS.getMethod("executeSync", Runnable.class);
			if (EXECUTE_SYNC.getReturnType() != void.class) {
				throw new NoSuchMethodException();
			}
		} catch (NoSuchMethodException err) {
			throw new IllegalStateException(err);
		}
	}

	public BukkitChatServer(BukkitParser parser, Server server) {
		this.parser = requireNonNull(parser);
		this.server = requireNonNull(server);
	}

	public Server getImpl() {
		return this.server;
	}

	public static Executor getNMS(Server server) {
		try {
			return (Executor) GET_HANDLE.invoke(server);
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		} catch (InvocationTargetException err) {
			Throwable target = err.getTargetException();
			if (target instanceof Error) throw (Error) target;
			if (target instanceof RuntimeException) throw (RuntimeException) target;
			throw new RuntimeException(err);
		}
	}

	public Executor getNMS() {
		return getNMS(this.server);
	}

	@Override
	public ChatServer getServer() {
		return this;
	}

	@Override
	public ChatPlayerList getPlayerList() {
		return this;
	}

	@Override
	public String getName() {
		return this.server.getConsoleSender().getName();
	}

	@Override
	public void sendMessage(ChatComponent component, ChatMessageType type, UUID uuid) {
		this.parser.sendMessage(this.server.getConsoleSender(), component, type, uuid);
	}

	@Override
	public Collection<? extends ChatPlayer> getPlayers() {
		return Collections2.transform(this.server.getOnlinePlayers(), this.parser::toChatPlayer);
	}

	@Override
	public ChatPlayer getPlayer(UUID uuid) {
		return this.parser.toChatPlayer(this.server.getPlayer(uuid));
	}

	@Override
	public ChatPlayer getPlayer(String name) {
		return this.parser.toChatPlayer(this.server.getPlayerExact(name));
	}

	@Override
	public void execute(Runnable command) {
		this.getNMS().execute(command);
	}

	@Override
	public void executeBlocking(Runnable command) {
		try {
			EXECUTE_SYNC.invoke(this.getNMS(), command);
		} catch (IllegalAccessException err) {
			throw new IllegalStateException(err);
		} catch (InvocationTargetException err) {
			Throwable target = err.getTargetException();
			if (target instanceof Error) throw (Error) target;
			if (target instanceof RuntimeException) throw (RuntimeException) target;
			throw new RuntimeException(err);
		}
	}

	@Deprecated
	@Override
	public void execute(IChatPlugin plugin, Runnable runnable) {
		this.parser.execute(this.server.getScheduler(), plugin, runnable);
	}

	@Override
	public void broadcastMessage(ChatComponent component, ChatMessageType type, UUID uuid, Predicate<? super ChatPlayer> predicate) {
		this.parser.broadcastMessage(this.server, component, type, uuid, predicate);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			BukkitChatServer other = (BukkitChatServer) obj;
			return this.parser.equals(other.parser) && this.server.equals(other.server);
		} else {
			return false;
		}
	}
}
